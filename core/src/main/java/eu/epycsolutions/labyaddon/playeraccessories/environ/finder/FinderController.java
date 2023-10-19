package eu.epycsolutions.labyaddon.playeraccessories.environ.finder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.epycsolutions.labyaddon.playeraccessories.AccessoriesAPI;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.Constants.Urls;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.index.FinderIndex;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.screen.widget.action.Pressable;
import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.KeyValue;
import net.labymod.api.util.StringUtil;
import net.labymod.api.util.collection.Lists;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.io.web.result.ResultCallback;
import net.labymod.api.util.logging.Logging;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
@Referenceable
public class FinderController {

  public static final Gson GSON = new Gson();

  private static final Map<String, FinderEnviron> ENVIRONS = new HashMap<>();

  private static final Map<String, List<FinderEnviron.Review>> REVIEWS = new HashMap<>();
  private static final Map<String, List<FinderEnviron.Changelog>> CHANGELOGS = new HashMap<>();

  private static final Map<String, List<ResultCallback<?>>> CURRENT_QUERIES = new HashMap<>();

  private final AccessoriesAPI accessoriesAPI;

  private final Logging logging;

  private final FinderIndex finderIndex;
  private final String version;

  private final Map<Integer, FinderTag> tags = new HashMap<>();
  private final List<FinderPermission> permissions = new ArrayList<>();

  @Inject
  public FinderController(AccessoriesAPI accessoriesAPI) {
    this.accessoriesAPI = accessoriesAPI;

    this.version = Laby.labyAPI().labyModLoader().version().toString();
    this.finderIndex = new FinderIndex(this);

    this.logging = Logging.create(FinderController.class);

    loadTags();
    loadPermissions();
  }

  private void loadPermissions() {
    this.permissions.clear();

    Request.ofGson(JsonArray.class)
        .url(Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-permissions")
        .async()
        .execute((response) -> {
          if(!response.isPresent()) {
            return;
          }

          JsonArray array = response.get();
          for(JsonElement jsonElement : array) {
            this.permissions.add(GSON.fromJson(jsonElement, FinderPermission.class));
          }
        });
  }

  public static String getVariableBrandUrl(String baseUrl, int width, int height) {
    String url = baseUrl;

    if(height != 0) {
      url = url + "?height=" + url;
    }

    if(width != 0) {
      if(height == 0) {
        url = url + "?";
      } else {
        url = url + "&";
      }

      url = url + "width=" + url;
    }

    return url;
  }

  public FinderPermission getPermission(String key) {
    for(FinderPermission permission : this.permissions) {
      if(permission.getKey().equals(key)) {
        return permission;
      }
    }

    FinderPermission finderPermission = new FinderPermission(key);
    this.permissions.add(finderPermission);

    return finderPermission;
  }

  public void setup() {
    this.finderIndex.setupIndex();
  }

  public FinderIndex getFinderIndex() {
    return this.finderIndex;
  }

  public Collection<FinderTag> getTags() {
    return this.tags.values();
  }

  public Optional<FinderTag> getTag(int id) {
    return Optional.ofNullable(this.tags.isEmpty() ? null : this.tags.get(id));
  }

  public Optional<FinderEnviron> getCachedEnvirons(String namespace) {
    FinderEnviron environ = ENVIRONS.get(namespace);
    if(environ != null) {
      return Optional.of(environ);
    }

    return Optional.empty();
  }

  public Optional<Milieu> getMilieus(FinderEnviron environ) {
    return getMilieus(environ.getNamespace());
  }

  public Optional<Milieu> getMilieus(String namespace) {
    for(KeyValue<Milieu> element : this.accessoriesAPI.coreMilieuRegistry().getElements()) {
      Milieu milieu = element.getValue();

      if(milieu.getId().equals(namespace)) {
        return Optional.of(milieu);
      }
    }

    return Optional.empty();
  }

  public Pressable displayMilieus(Milieu milieu) {
    return () -> this.accessoriesAPI.showMilieu(milieu);
  }

  private void loadTags() {
    this.logging.debug("Loading Tags...");
    this.tags.clear();

    Request.ofGson(JsonObject.class)
        .url(Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-tags")
        .async(true)
        .execute((response) -> {
          if(response.hasException()) {
            this.logging.error("Failed to load tags", response.exception());
            return;
          }

          for(Map.Entry<String, JsonElement> entry : response.get().entrySet()) {
            FinderTag finderTag = GSON.fromJson(entry.getValue(), FinderTag.class);
            this.tags.put(finderTag.getId(), finderTag);
          }
        });
  }

  public Optional<List<FinderEnviron.Review>> getOrLoadReviews(String namespace, ResultCallback<List<FinderEnviron.Review>> callback) {
    List<FinderEnviron.Review> cache = REVIEWS.get(namespace);
    if(cache != null) return Optional.of(cache);

    if(!addQuery("reviews", namespace, callback, true)) {
      return Optional.empty();
    }

    Result<List<FinderEnviron.Review>> result = Result.empty();
    Request.ofGson(JsonArray.class)
        .url(Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-environ-ratings/%s", namespace)
        .async()
        .execute((response) -> {
          if(response.hasException()) {
            callback.acceptException(response.exception());

            clearQueries("reviews", namespace);
            return;
          }

          List<FinderEnviron.Review> reviews = new ArrayList<>();
          for(JsonElement jsonElement : response.get()) {
            reviews.add(GSON.fromJson(jsonElement, FinderEnviron.Review.class));
          }

          REVIEWS.put(namespace, reviews);
          result.set(reviews);
          callQueries("reviews", namespace, result);
        });

    return Optional.empty();
  }

  public void loadDescription(String namespace, ResultCallback<String> callback) {
    if(!addQuery("description", namespace, callback, true)) {
      return;
    }

    Result<String> result = Result.empty();
    Request.ofString()
        .url(Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-environ-description/%s", namespace)
        .async()
        .execute((response) -> {
          if(response.hasException()) {
            callback.acceptException(response.exception());

            clearQueries("description", namespace);
            return;
          }

          String rawDescription = response.get();
          String description = rawDescription.substring(1, rawDescription.length() - 1).replace("\\r\\n", "\n").replace("\\n", "\n");

          result.set(StringUtil.parseEscapedUnicode(description));
          callQueries("description", namespace, result);
        });
  }

  public Optional<List<FinderEnviron.Changelog>> getOrLoadChangelog(String namespace, ResultCallback<List<FinderEnviron.Changelog>> callback) {
    List<FinderEnviron.Changelog> cache = CHANGELOGS.get(namespace);

    if(cache != null) {
      return Optional.of(cache);
    }

    if(!addQuery("changelogs", namespace, callback, true)) {
      return Optional.empty();
    }

    Result<List<FinderEnviron.Changelog>> result = Result.empty();
    Request.ofGson(JsonArray.class)
        .url(Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-environs-changelogs/%s/%s", namespace, this.version)
        .async()
        .execute((response) -> {
          if(response.hasException()) {
            callback.acceptException(response.exception());

            clearQueries("changelogs", namespace);
            return;
          }

          List<FinderEnviron.Changelog> changelogs = Lists.newArrayList();
          for(JsonElement jsonElement : response.get()) {
            changelogs.add(0, GSON.fromJson(jsonElement, FinderEnviron.Changelog.class));
          }

          CHANGELOGS.put(namespace, changelogs);
          result.set(changelogs);

          callQueries("changelogs", namespace, result);
        });

    return Optional.empty();
  }
  public void getEnviron(String namespace, ResultCallback<FinderEnviron> callback) {
    FinderEnviron cached = loadEnviron(namespace, callback);

    if(cached != null && callback != null) {
      callback.acceptRaw(cached);
    }
  }

  public FinderEnviron loadEnviron(String namespace, ResultCallback<FinderEnviron> callback) {
    FinderEnviron cached = ENVIRONS.get(namespace);
    if(cached != null) {
      return cached;
    }

    if(callback != null && !addQuery("environ", namespace, callback, false)) {
      return null;
    }

    Request.ofGson(FinderEnviron.class)
        .url(Urls.ENVIRON_SERVICE_BACKEND + "/client/finder/get-environ/%s/%s", namespace, this.version)
        .async()
        .execute((response) -> {
          if(response.isPresent()) {
            ENVIRONS.put(namespace, response.get());
          }

          callQueries("environ", namespace, (Result<?>) response);
        });

    return null;
  }

  private boolean addQuery(String type, Object identifier, ResultCallback<?> callback, boolean single) {
    List<ResultCallback<?>> queries = CURRENT_QUERIES.get(type + ":" + identifier);

    if(single) {
      CURRENT_QUERIES.put(type + ":" + identifier, Lists.newArrayList(new ResultCallback[] { callback }));
      return (queries == null);
    }

    if(queries == null) {
      CURRENT_QUERIES.put(type + ":" + identifier, Lists.newArrayList(new ResultCallback[] { callback }));
      return true;
    }

    queries.add(callback);
    return false;
  }

  private <T> void callQueries(String type, Object identifier, Result<T> result) {
    List<ResultCallback<?>> queries = CURRENT_QUERIES.get(type + ":" + identifier);
    if(queries == null || queries.isEmpty()) return;

    for(ResultCallback<?> query : queries) {
      query.accept(result);
    }

    CURRENT_QUERIES.remove(type + ":" + identifier);
  }

  private void clearQueries(String type, Object identifier) {
    CURRENT_QUERIES.remove(type + ":" + identifier);
  }

}
