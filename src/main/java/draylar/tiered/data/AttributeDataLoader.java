package draylar.tiered.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import draylar.tiered.Tiered;
import draylar.tiered.api.AttributeTemplate;
import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AttributeDataLoader implements SimpleSynchronousResourceReloadListener {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(AttributeTemplate.RawModifier.class, new EntityAttributeModifierDeserializer())
            .registerTypeAdapter(AttributeTemplate.RawModifier.class, new EntityAttributeModifierSerializer())
            .registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotSerializer())
            .registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotDeserializer())
            .registerTypeAdapter(Formatting.class, new FormattingDeserializer())
            .registerTypeAdapter(TextColor.class, new TextColorDeserializer())
            .registerTypeHierarchyAdapter(Style.class, new StyleDeserializer())
            .create();

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String[] DEFAULT_ATTRIBUTE_FILES = new String[]{
            "data/tiered/item_attributes/all/common.json",
            "data/tiered/item_attributes/all/epic.json",
            "data/tiered/item_attributes/all/legendary.json",
            "data/tiered/item_attributes/all/rare.json",
            "data/tiered/item_attributes/all_armor/dented.json",
            "data/tiered/item_attributes/all_armor/fortified.json",
            "data/tiered/item_attributes/all_armor/heavy.json",
            "data/tiered/item_attributes/all_armor/reinforced.json",
            "data/tiered/item_attributes/all_armor/resilient.json",
            "data/tiered/item_attributes/all_armor/unchained.json",
            "data/tiered/item_attributes/all_tools/extended.json",
            "data/tiered/item_attributes/fishing_rods/lucky.json",
            "data/tiered/item_attributes/gathering_tools/hasteful.json",
            "data/tiered/item_attributes/gathering_tools/swift.json",
            "data/tiered/item_attributes/melee_weapons/berserk.json",
            "data/tiered/item_attributes/melee_weapons/critical.json",
            "data/tiered/item_attributes/melee_weapons/dull.json",
            "data/tiered/item_attributes/melee_weapons/keen.json",
            "data/tiered/item_attributes/melee_weapons/sharp.json"
    };

    private Map<Identifier, PotentialAttribute> itemAttributes = new HashMap<>();

    public AttributeDataLoader() {
        loadDefaults();
    }

    public void loadDefaults() {
        for (String path : DEFAULT_ATTRIBUTE_FILES) {
            try (var is = AttributeDataLoader.class.getClassLoader().getResourceAsStream(path)) {
                if (is != null) {
                    try (var reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        PotentialAttribute pa = GSON.fromJson(reader, PotentialAttribute.class);
                        if (pa != null && pa.getID() != null) {
                            Identifier id = Identifier.tryParse(pa.getID());
                            if (id != null) {
                                itemAttributes.put(id, pa);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to load default attribute: " + path, e);
            }
        }
        LOGGER.info("Preloaded {} default tiered item attributes", itemAttributes.size());
    }

    @Override
    public Identifier getFabricId() {
        return Tiered.id("item_attributes");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Identifier, PotentialAttribute> readItemAttributes = Maps.newHashMap();
        String dataType = "item_attributes";

        manager.findResources(dataType, path -> path.getPath().endsWith(".json")).forEach((resourceLocation, resource) -> {
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                PotentialAttribute itemAttribute = GSON.fromJson(jsonElement, PotentialAttribute.class);
                Identifier id = Identifier.tryParse(itemAttribute.getID());
                if (id != null) {
                    readItemAttributes.put(id, itemAttribute);
                }
            } catch (Exception exception) {
                LOGGER.error("Parsing error loading tiered attribute {}", resourceLocation, exception);
            }
        });

        if (!readItemAttributes.isEmpty()) {
            itemAttributes.putAll(readItemAttributes);
        }
        LOGGER.info("Loaded {} tiered item attributes from resources", itemAttributes.size());
    }

    public Map<Identifier, PotentialAttribute> getItemAttributes() {
        return itemAttributes;
    }
}
