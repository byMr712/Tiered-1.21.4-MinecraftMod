package draylar.tiered.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public class TextColorDeserializer implements JsonDeserializer<TextColor> {

    @Override
    public TextColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String str = json.getAsString();
        TextColor parsed = TextColor.parse(str).result().orElse(null);
        if (parsed != null) {
            return parsed;
        }
        Formatting formatting = Formatting.byName(str.toLowerCase());
        if (formatting != null) {
            return TextColor.fromFormatting(formatting);
        }
        return TextColor.fromRgb(0xFFFFFF);
    }
}
