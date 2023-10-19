package eu.epycsolutions.labyaddon.playeraccessories.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.epycsolutions.labyaddon.playeraccessories.Accessories;
import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.io.web.result.ResultCallback;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;

public interface JsonFileCache<T extends JsonElement> {

  static <T extends JsonElement> JsonFileCache<T> create(@NotNull Path path, @NotNull String url, @NotNull String name, @NotNull Class<T> clazz) {
    return Accessories.references().jsonFileCacheFactory().create(path, url, name, clazz);
  }

  static <T extends JsonElement> JsonFileCache<T> create(@NotNull Path path, @NotNull Request<T> request, @NotNull String name) {
    return Accessories.references().jsonFileCacheFactory().create(path, request, name);
  }

  void read(boolean async, ResultCallback<T> callback);

  void update(boolean async, ResultCallback<T> callback);

  void download(boolean async, ResultCallback<T> callback);

  boolean isUpToDate();

  JsonFileCache<T> readLastModifiedLongFromHeader(String headerKey);

  JsonFileCache<T> readLastModifiedLongFromHeader(String headerKey, LongConsumer consumer);

  JsonFileCache<T> readLastModifiedDateFromHeader(String headerKey, SimpleDateFormat dateFormat);

  JsonFileCache<T> readLastModifiedDateFromHeader(String headerKey, SimpleDateFormat dateFormat, LongConsumer consumer);

  JsonFileCache<T> readLastModifiedFromUrl(String url, ToLongFunction<Result<String>> parser);

  JsonFileCache<T> readLastModifiedFromUrl(String url, ToLongFunction<Result<String>> parser, LongConsumer callback);

  JsonFileCache<T> setLastModified(long lastModified);

  <R> R deserialize(Class<R> clazz);

  Result<JsonObject> getJsonObject();

  @Referenceable
  interface Factory {
    @NotNull
    <T extends JsonElement> JsonFileCache<T> create(Path path, String url, String name, Class<T> clazz);

    @NotNull
    <T extends JsonElement> JsonFileCache<T> create(@NotNull Path path, @NotNull Request<T> request, String name);
  }

}
