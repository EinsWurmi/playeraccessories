package eu.epycsolutions.labyaddon.playeraccessories.util.jsonfilecache;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import eu.epycsolutions.labyaddon.playeraccessories.util.JsonFileCache;
import net.labymod.api.Laby;
import net.labymod.api.util.io.IOUtil;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.WebResolver;
import net.labymod.api.util.io.web.request.types.GsonRequest;
import net.labymod.api.util.io.web.request.types.StringRequest;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.io.web.result.ResultCallback;
import net.labymod.api.util.time.TimeUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;

public class DefaultJsonFileCache<T extends JsonElement> implements JsonFileCache<T> {

  private static final WebResolver WEB_RESOLVER = Laby.references().webResolver();
  private static final Gson GSON = new Gson();

  private final Path path;
  private final String name;
  private final Request<T> request;
  private final Class<T> type;
  private Result<JsonObject> latestCache;
  private Long lastModified;

  protected DefaultJsonFileCache(Path path, Request<T> request, String name) {
    if(!(request instanceof GsonRequest)) {
      throw new UnsupportedOperationException("Request has to be a GsonRequest!");
    }

    this.path = path;
    this.request = request;
    this.name = name;

    this.type = ((GsonRequest) request).getType();
    this.lastModified = Long.valueOf(0L);
    this.latestCache = Result.empty();
  }

  @Override
  public void read(boolean async, ResultCallback<T> callback) {
    JsonObject cachedFile;
    if(!IOUtil.exists(this.path)) {
      download(async, callback);
      return;
    }

    try {
      String rawJson = new String(Files.readAllBytes(this.path), StandardCharsets.UTF_8);
      cachedFile = GSON.fromJson(rawJson, JsonObject.class);
    } catch(IOException exception) {
      exception.printStackTrace();

      download(async, callback);
      return;
    }

    if(isUpToDate(cachedFile)) {
      String name = getName(true);

      if(cachedFile.has(name)) {
        try {
          this.latestCache = Result.of(cachedFile);

          JsonElement jsonElement = cachedFile.get(name);
          if(jsonElement.isJsonArray()) {
            callback.acceptRaw(GSON.fromJson(jsonElement, this.type));
          } else {
            callback.acceptRaw(GSON.fromJson(cachedFile, this.type));
          }

          return;
        } catch(JsonSyntaxException exception) {
          exception.printStackTrace();
        }
      }
    }

    download(async, callback);
  }

  @Override
  public void update(boolean async, ResultCallback<T> callback) {
    Result<JsonObject> latestResult = this.latestCache;
    if(!latestResult.isPresent()) {
      read(async, callback);
      return;
    }

    if(!isUpToDate()) {
      download(async, (result) -> {
        if(result.isPresent()) {
          callback.accept(result);
          return;
        }

        this.latestCache = latestResult;
      });
    }
  }

  @Override
  public void download(boolean async, ResultCallback<T> callback) {
    WEB_RESOLVER.resolveConnection(this.request.copy().async(async), (response) -> {
      if(response.hasException()) {
        callback.acceptException(response.exception());
        return;
      }

      JsonElement jsonElement = response.get();
      DefaultJsonFileCache<T> fileCache = this;
      fileCache.latestCache = Result.of(fileCache.saveToFile(jsonElement));
      callback.acceptRaw((T) jsonElement);
    });
  }

  @Override
  public boolean isUpToDate() {
    if(!this.latestCache.isPresent()) {
      return false;
    }

    return isUpToDate(this.latestCache.get());
  }

  @Override
  public JsonFileCache<T> readLastModifiedLongFromHeader(String headerKey) {
    return readLastModifiedLongFromHeader(headerKey, null);
  }

  @Override
  public JsonFileCache<T> readLastModifiedLongFromHeader(String headerKey, LongConsumer consumer) {
    sendHeaderRequest((consumer != null), headerKey, (result) -> {
      if(result.isPresent()) {
        try {
          this.lastModified = Long.parseLong(result.get());
        } catch(NumberFormatException exception) {
          this.lastModified = 0L;
        }

        if(consumer != null) {
          consumer.accept(this.lastModified);
        }
      }
    });

    return this;
  }

  @Override
  public JsonFileCache<T> readLastModifiedDateFromHeader(String headerKey, SimpleDateFormat dateFormat) {
    return readLastModifiedDateFromHeader(headerKey, dateFormat, null);
  }

  @Override
  public JsonFileCache<T> readLastModifiedDateFromHeader(String headerKey, SimpleDateFormat dateFormat, LongConsumer consumer) {
    sendHeaderRequest((consumer != null), headerKey, (result) -> {
      if(result.isPresent()) {
        try {
          this.lastModified = dateFormat.parse(result.get()).getTime();
        } catch(ParseException exception) {
          this.lastModified = 0L;
        }

        if(consumer != null) {
          consumer.accept(this.lastModified);
        }
      }
    });

    return this;
  }

  @Override
  public JsonFileCache<T> readLastModifiedFromUrl(String url, ToLongFunction<Result<String>> parser) {
    return readLastModifiedFromUrl(url, parser, null);
  }

  @Override
  public JsonFileCache<T> readLastModifiedFromUrl(String url, ToLongFunction<Result<String>> parser, LongConsumer callback) {
    StringRequest stringRequest = Request.ofString();
    stringRequest.url(url);
    stringRequest.async((callback != null));

    WEB_RESOLVER.resolveConnection(stringRequest, (response) -> {
      long lastModified;

      try {
        lastModified = parser.applyAsLong(response);
      } catch(Exception exception) {
        lastModified = 0L;
      }

      String string = String.valueOf(lastModified);

      if(string.length() == 10) {
        lastModified *= 1000L;
      }

      this.lastModified = lastModified;
      if(callback != null) {
        callback.accept(lastModified);
      }
    });

    return this;
  }

  @Override
  public JsonFileCache<T> setLastModified(long lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  @Override
  public <R> R deserialize(Class<R> clazz) {
    String name = getName(true);
    if(!this.latestCache.isPresent() || !((JsonObject) this.latestCache.get()).has(name)) {
      return null;
    }

    try {
      JsonElement jsonElement = null;
      JsonObject jsonObject = this.latestCache.get();
      if(this.name == null || this.name.isEmpty()) {
        jsonElement = jsonObject.get(name);
      }

      return GSON.fromJson(jsonElement, clazz);
    } catch(Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public Result<JsonObject> getJsonObject() {
    return this.latestCache;
  }

  private JsonObject saveToFile(JsonElement jsonElement) {
    if(jsonElement.isJsonObject()) {
      String name = getName(false);

      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if (name != null && jsonObject.has(name) && jsonObject.entrySet().size() == 1) {
        jsonElement = jsonObject.get(name);
      }
    }

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("updated_at", TimeUtil.getCurrentTimeMills());
    jsonObject.add(getName(true), jsonElement);

    try {
      if(!IOUtil.exists(this.path)) {
        IOUtil.createDirectories(this.path.getParent());
        IOUtil.createFile(this.path);
      }

      Files.write(this.path, GSON.toJson(jsonObject).getBytes(StandardCharsets.UTF_8));
    } catch(IOException exception) {
      exception.printStackTrace();
    }

    return jsonObject;
  }

  private boolean isUpToDate(JsonObject jsonObject) {
    if(jsonObject == null || !jsonObject.has("updated_at")) {
      return false;
    }

    if(this.lastModified == 0L) {
      return true;
    }

    return (jsonObject.get("updated_at").getAsLong() > this.lastModified);
  }

  private void sendHeaderRequest(boolean async, String key, ResultCallback<String> callback) {
    Request<T> request = this.request.copy();
    request.async(async);
    request.method(Request.Method.HEAD);

    WEB_RESOLVER.resolveConnection(request, (response) -> {
      if(response.hasException()) {
        callback.acceptException(response.exception());
        return;
      }

      if(response.getStatusCode() != 200) {
        callback.acceptException(new UnsupportedOperationException("Response code is not 200 (was " + response.getStatusCode() + ")"));
        return;
      }

      String header = response.getHeaders().get(key);
      if(header == null) {
        callback.acceptException(new UnsupportedOperationException("Header \"" + key + "\" not found."));
        return;
      }

      callback.acceptRaw(header);
    });
  }

  private String getName(boolean cache) {
    if(this.name != null && !this.name.isEmpty()) {
      return this.name;
    }

    return cache ? "cache" : null;
  }

  public static Gson getGson() {
    return GSON;
  }

}
