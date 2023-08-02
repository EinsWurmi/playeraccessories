package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.labymod.api.util.PrimitiveHelper;
import net.labymod.api.util.reflection.Reflection;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ConfigPropertyTypeAdapter implements JsonSerializer<ConfigProperty<?>>, JsonDeserializer<Object> {

  @Override
  public JsonElement serialize(ConfigProperty<?> src, Type typeOfSrc, JsonSerializationContext context) {
    if(typeOfSrc instanceof ParameterizedType parameterizedType) {
      if(Reflection.isType(parameterizedType, PrimitiveHelper.BOOLEAN)) return new JsonPrimitive((Boolean) src.get());
      if(Reflection.isType(parameterizedType, PrimitiveHelper.NUMBER_PRIMITIVES)) return new JsonPrimitive((Number) src.get());
      if(Reflection.isType(parameterizedType, String.class)) return new JsonPrimitive((String) src.get());
      if(Reflection.isType(parameterizedType, Character.class)) return new JsonPrimitive((Character) src.get());

      return context.serialize(src.get());
    }

    return null;
  }

  @Override
  public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if(typeOfT instanceof ParameterizedType parameterizedType) {
      Type actialType = parameterizedType.getActualTypeArguments()[0];
      return context.deserialize(json, actialType);
    }

    return null;
  }
}
