package eu.epycsolutions.labyaddon.playeraccessories.util.jsonfilecache;

import com.google.gson.JsonElement;
import eu.epycsolutions.labyaddon.playeraccessories.model.Implements;
import eu.epycsolutions.labyaddon.playeraccessories.util.JsonFileCache;
import net.labymod.api.util.io.web.request.AbstractRequest;
import net.labymod.api.util.io.web.request.Request;
import org.jetbrains.annotations.NotNull;
import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
@Implements(JsonFileCache.Factory.class)
public class DefaultJsonFileCacheFactory implements JsonFileCache.Factory {

  private static final DefaultJsonFileCacheFactory INSTANCE = new DefaultJsonFileCacheFactory();

  public static <T extends JsonElement> JsonFileCache<T> createJsonFileCache(Path path, String url, String name, Class<T> clazz) {
    return INSTANCE.create(path, url, name, clazz);
  }

  public static <T extends JsonElement> JsonFileCache<T> createJsonFileCache(Path path, Request<T> request, String name) {
    return INSTANCE.create(path, request, name);
  }

  @Override
  public @NotNull <T extends JsonElement> JsonFileCache<T> create(Path path, String url, String name, Class<T> clazz) {
    AbstractRequest abstractRequest = Request.ofGson(clazz).url(url);
    return new DefaultJsonFileCache<>(path, abstractRequest, name);
  }

  @Override
  public @NotNull <T extends JsonElement> JsonFileCache<T> create(@NotNull Path path, @NotNull Request<T> request, String name) {
    return new DefaultJsonFileCache<>(path, request, name);
  }
}
