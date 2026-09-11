package draylar.tiered.gson;

import com.google.gson.*;
import draylar.tiered.api.AttributeTemplate;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.lang.reflect.Type;

public class EntityAttributeModifierDeserializer implements JsonDeserializer<AttributeTemplate.RawModifier> {

    @Override
    public AttributeTemplate.RawModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "tiered:modifier";
        double amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsDouble() : 0.0;
        String opString = jsonObject.has("operation") ? jsonObject.get("operation").getAsString().toUpperCase() : "ADD_VALUE";

        EntityAttributeModifier.Operation operation;
        switch (opString) {
            case "ADDITION":
            case "ADD_VALUE":
                operation = EntityAttributeModifier.Operation.ADD_VALUE;
                break;
            case "MULTIPLY_BASE":
            case "ADD_MULTIPLIED_BASE":
                operation = EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                break;
            case "MULTIPLY_TOTAL":
            case "ADD_MULTIPLIED_TOTAL":
                operation = EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
                break;
            default:
                operation = EntityAttributeModifier.Operation.ADD_VALUE;
                break;
        }

        return new AttributeTemplate.RawModifier(name, amount, operation);
    }
}
