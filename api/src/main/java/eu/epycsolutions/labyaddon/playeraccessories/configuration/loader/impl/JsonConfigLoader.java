package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigLoadException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigSaveException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.Exclude;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigPropertyTypeAdapter;
import eu.epycsolutions.labyaddon.playeraccessories.events.JsonConfigLoaderInitializeEvent;
import net.labymod.api.Laby;
import net.labymod.api.util.io.IOUtil;
import net.labymod.api.util.reflection.Reflection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonConfigLoader extends AbstractConfigLoader {

  private final Gson gson;

  public JsonConfigLoader(Path directory) {
    this(directory, new GsonBuilder().setPrettyPrinting());
  }

  public JsonConfigLoader(Path directory, GsonBuilder builder) {
    super(directory);

    Laby.fireEvent(new JsonConfigLoaderInitializeEvent(builder));
    builder.registerTypeAdapter(ConfigProperty.class, new ConfigPropertyTypeAdapter());

    this.gson = builder.create();
  }

  @Override
  public <T extends ConfigAccessor> T load(Class<T> clazz) throws ConfigLoadException {
    Path path = getPath(clazz);

    try {
      if(IOUtil.exists(path)) {
        try(BufferedReader reader = Files.newBufferedReader(path)) {
          JsonObject jsonObject = this.gson.fromJson(reader, JsonObject.class);
          JsonObject converted = getConvertedConfigJson(clazz, jsonObject);

          T config = this.gson.fromJson(converted, clazz);
          return config == null ? loadConfig(clazz) : config;
        }
      } else {
        return loadConfig(clazz);
      }
    } catch(Exception exception) {
      throw new ConfigLoadException(clazz, exception);
    }
  }

  private <T extends ConfigAccessor> JsonObject getConvertedConfigJson(Class<T> clazz, JsonObject jsonObject) {
    try {
      Reflection.getFields(clazz, false, (member) -> {
        if(member.isAnnotationPresent(Exclude.class) || !jsonObject.has(member.getName())) return;

        JsonElement jsonElement = jsonObject.get(member.getName());
        if(!jsonElement.isJsonObject() || !ConfigAccessor.class.isAssignableFrom(member.getType())) {
          Class<?> type = member.getType();
          Type genericType = member.getGenericType();

          if(ConfigProperty.class.isAssignableFrom(type) && genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            for(Type actualTypeArgument : actualTypeArguments) {
              if(actualTypeArgument instanceof Class) {
                type = (Class<?>) actualTypeArgument;
                genericType = type;
                break;
              } else if(actualTypeArgument instanceof ParameterizedType) {
                ParameterizedType subType = (ParameterizedType) actualTypeArgument;
                type = (Class<?>) subType.getRawType();
                genericType = subType;
                break;
              }
            }
          }

          if(jsonElement.isJsonArray() && List.class.isAssignableFrom(type)) {
            Class<? extends ConfigAccessor> accessorClass = null;

            if(genericType instanceof ParameterizedType) {
              ParameterizedType parameterizedType = (ParameterizedType) genericType;
              Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

              if(actualTypeArguments.length != 0 && actualTypeArguments[0] instanceof Class) {
                Class<?> genericClass = (Class<?>) actualTypeArguments[0];
                if(ConfigAccessor.class.isAssignableFrom(genericClass)) accessorClass = (Class<? extends ConfigAccessor>) genericClass;
              }
            }

            if(accessorClass != null) {
              JsonArray newArray = new JsonArray();

              for(JsonElement element : jsonElement.getAsJsonArray()) {
                if(element.isJsonObject()) {
                  newArray.add(getConvertedConfigJson(accessorClass, element.getAsJsonObject()));
                } else {
                  return;
                }
              }

              jsonObject.add(member.getName(), newArray);
            }

            return;
          }

          return;
        }

        Class<? extends ConfigAccessor> configAccessorClass = (Class<? extends ConfigAccessor>) member.getType();
        jsonObject.add(member.getName(), getConvertedConfigJson(configAccessorClass, jsonElement.getAsJsonObject()));
      });
    } catch(Exception exception) {
      exception.printStackTrace();
    }

    return jsonObject;
  }

  private <T extends ConfigAccessor> T loadConfig(Class<T> clazz) throws ReflectiveOperationException {
    T config = createInstance(clazz);

    save(config);
    return config;
  }

  private <T extends ConfigAccessor> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
    Constructor<T> constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);

    return constructor.newInstance();
  }

  @Override
  public void save(ConfigAccessor config) throws ConfigSaveException {
    Path path = getPath(config.getClass());
    Path parent = path.getParent();

    try {
      if(!IOUtil.exists(parent)) IOUtil.createDirectories(parent);

      try(BufferedWriter writer = Files.newBufferedWriter(parent)) {
        this.gson.toJson(config, writer);
      }
    } catch(Exception exception) {
      throw new ConfigSaveException(config, exception);
    }
  }

  @Override
  public Object serialize(ConfigAccessor config) throws Exception {
    return this.gson.toJsonTree(config);
  }

  @Override
  public String getFileExtension() {
    return "json";
  }

  public Gson getGson() {
    return this.gson;
  }

  public static JsonConfigLoader createDefault() {
    return new JsonConfigLoader(defaultDirectory());
  }

}