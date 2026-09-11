package draylar.tiered.gson;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public class StyleDeserializer implements JsonDeserializer<Style>, JsonSerializer<Style> {

    @Override
    public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            Style style = Style.EMPTY;
            if (obj.has("color")) {
                String colorStr = obj.get("color").getAsString();
                TextColor color = TextColor.parse(colorStr).result().orElse(null);
                if (color == null) {
                    Formatting formatting = Formatting.byName(colorStr.toLowerCase());
                    if (formatting != null) {
                        color = TextColor.fromFormatting(formatting);
                    }
                }
                if (color != null) {
                    style = style.withColor(color);
                }
            }
            if (obj.has("bold")) {
                style = style.withBold(obj.get("bold").getAsBoolean());
            }
            if (obj.has("italic")) {
                style = style.withItalic(obj.get("italic").getAsBoolean());
            }
            if (obj.has("underlined")) {
                style = style.withUnderline(obj.get("underlined").getAsBoolean());
            }
            if (obj.has("strikethrough")) {
                style = style.withStrikethrough(obj.get("strikethrough").getAsBoolean());
            }
            if (obj.has("obfuscated")) {
                style = style.withObfuscated(obj.get("obfuscated").getAsBoolean());
            }
            return style;
        }
        return Style.Codecs.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(Style.EMPTY);
    }

    @Override
    public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
        return Style.Codecs.CODEC.encodeStart(JsonOps.INSTANCE, src).result().orElse(new JsonObject());
    }
}
