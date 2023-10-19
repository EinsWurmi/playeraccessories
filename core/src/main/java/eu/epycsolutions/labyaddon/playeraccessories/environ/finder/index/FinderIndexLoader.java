package eu.epycsolutions.labyaddon.playeraccessories.environ.finder.index;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.Constants.AccessoriesFiles;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.Constants.Urls;
import eu.epycsolutions.labyaddon.playeraccessories.util.JsonFileCache;
import eu.epycsolutions.labyaddon.playeraccessories.util.jsonfilecache.DefaultJsonFileCacheFactory;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.io.web.result.ResultCallback;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FinderIndexLoader {

  private static FinderIndexLoader instance;

  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

  private final JsonFileCache<JsonArray> indexFileCache;

  private ResultCallback<JsonArray> callback;
  private Result<JsonArray> latestIndex;

  private FinderIndexLoader() {
    this.indexFileCache = DefaultJsonFileCacheFactory
        .createJsonFileCache(
            AccessoriesFiles.ENVIRONS_INDEX,
        Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-index",
            "index",
            JsonArray.class
        )
        .readLastModifiedDateFromHeader("Last-Modified", DATE_FORMAT);

    setupIndex();
  }

  public static FinderIndexLoader getInstance() {
    if(instance == null) {
      instance = new FinderIndexLoader();
    }

    return instance;
  }

  private void setupIndex() {
    this.indexFileCache.read(false, this::handleResult);
  }

  public Result<JsonArray> getLatestIndex() {
    return (this.latestIndex == null) ? Result.empty() : this.latestIndex;
  }

  public void addCallback(ResultCallback<JsonArray> callback) {
    this.callback = callback;
    if(this.latestIndex != null) {
      this.callback.accept(this.latestIndex);
    }
  }

  public JsonFileCache<JsonArray> getIndexFileCache() {
    return this.indexFileCache;
  }

  public void handleResult(Result<JsonArray> result) {
    if(result.isPresent()) {
      this.latestIndex = result;

      if(this.callback != null) {
        this.callback.accept(result);
      }
    }
  }

  public JsonObject getEnvironObject(String namespace) {
    if (!this.latestIndex.isPresent()) {
      return null;
    }

    for(JsonElement jsonElement : this.latestIndex.get()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if(jsonObject.get("namespace").getAsString().equals(namespace)) {
        return jsonObject;
      }
    }

    return null;
  }

}
