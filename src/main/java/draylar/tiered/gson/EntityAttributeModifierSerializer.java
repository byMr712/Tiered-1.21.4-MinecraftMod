package draylar.tiered.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import draylar.tiered.api.AttributeTemplate;

import java.lang.reflect.Type;

public class EntityAttributeModifierSerializer implements JsonSerializer<AttributeTemplate.RawModifier> {

    @Override
    public JsonElement serialize(AttributeTemplate.RawModifier src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", src.getName());
        obj.addProperty("amount", src.getAmount());
        obj.addProperty("operation", src.getOperation().toString());
        return obj;
    }
}
