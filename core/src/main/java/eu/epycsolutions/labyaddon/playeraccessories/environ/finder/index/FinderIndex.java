package eu.epycsolutions.labyaddon.playeraccessories.environ.finder.index;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.epycsolutions.labyaddon.playeraccessories.environ.FinderSortBy;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderController;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderTag;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.EnvironMeta;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.labymod.api.Laby;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.collection.Lists;
import net.labymod.api.util.concurrent.task.Task;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.version.VersionMultiRange;
import net.labymod.api.util.version.serial.VersionDeserializer;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FinderIndex {

  private final FinderIndexLoader indexLoader;
  private final FinderController finderController;
  private final IndexFilter indexFilter;
  private final Version version;
  private List<FinderEnviron> indexEnvirons;

  public FinderIndex(FinderController controller) {
    this.finderController = controller;
    this.indexLoader = FinderIndexLoader.getInstance();

    this.version = VersionDeserializer.from(Laby.labyAPI().labyModLoader().version().toString());

    this.indexFilter = new IndexFilter(this);
    this.indexEnvirons = Lists.newArrayList();
  }

  public void setupIndex() {
    this.indexLoader.addCallback((result) -> {
      if(result.isPresent()) {
        this.indexEnvirons = convertToFinderEnvirons(result.get());
      }
    });

    Task
        .builder(() -> this.indexLoader.getIndexFileCache().readLastModifiedDateFromHeader("Last-Modified", FinderIndexLoader.DATE_FORMAT))
        .delay(1L, TimeUnit.HOURS)
        .repeat(2L, TimeUnit.HOURS)
        .build().execute();
  }

  public IndexFilter filter() {
    return this.indexFilter;
  }

  public List<FinderEnviron> getIndexEnvirons() {
    return this.indexEnvirons;
  }

  public Result<JsonArray> getIndex() {
    return this.indexLoader.getLatestIndex();
  }

  public List<FinderEnviron> convertToFinderEnvirons(JsonArray jsonArray) {
    List<FinderEnviron> environs = Lists.newArrayList();

    for(JsonElement jsonElement : jsonArray) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();

      String versionString = jsonObject.has("version_string") ? jsonObject.get("version_string").getAsString() : "*";
      VersionMultiRange versionMultiRange = new VersionMultiRange(versionString);
      if(!versionMultiRange.isCompatible(this.version)) {
        continue;
      }

      String namespace = jsonObject.get("namespace").getAsString();
      Optional<FinderEnviron> cachedEnvirons = this.finderController.getCachedEnvirons(namespace);

      int ranking = jsonObject.get("ranking").getAsInt();
      if(cachedEnvirons.isPresent()) {
        FinderEnviron environ = cachedEnvirons.get();
        environ.setRanking(ranking);
        environs.add(environ);

        continue;
      }

      environs.add(new FinderIndexEnviron(jsonObject));

      if(ranking < 7) {
        this.finderController.loadEnviron(namespace, null);
      }
    }

    return environs;
  }

  public class FinderIndexEnviron extends FinderEnviron {
    private static final FinderEnviron.Rating INDEX_RATING = new FinderEnviron.Rating(0, 0.0D);

    protected FinderIndexEnviron(JsonObject jsonObject) {
      this.name = jsonObject.get("name").getAsString();
      this.namespace = jsonObject.get("namespace").getAsString();
      this.shortDescription = jsonObject.get("short_description").getAsString();

      this.author = jsonObject.has("author") ? jsonObject.get("author").getAsString() : "Unknown";

      this.ranking = jsonObject.get("ranking").getAsInt();

      this.requiredLabyModBuild = jsonObject.has("required_labymod_build") ? jsonObject.get("required_labymod_build").getAsInt() : 0;
      this.tags = FinderController.GSON.fromJson(jsonObject.get("tags").getAsJsonArray(), int[].class);

      this.meta = FinderController.GSON.fromJson(jsonObject.get("meta").getAsJsonArray(), EnvironMeta[].class);
    }

    public FinderEnviron.Rating getRating() {
      return (this.rating == null) ? INDEX_RATING : this.rating;
    }
  }

  public class IndexFilter {
    private final FinderIndex index;

    private IndexFilter(FinderIndex index) {
      this.index = index;
    }

    public Optional<FinderEnviron> namespace(String namespace) {
      for(FinderEnviron environ : getEnvirons()) {
        if(environ.getNamespace().equals(namespace)) {
          return Optional.of(environ);
        }
      }

      return Optional.empty();
    }

    public List<FinderEnviron> search(FinderSortBy sortBy, String search) {
      String query = (search == null) ? null : search.toLowerCase().replace(" ", "");
      if(query == null || query.isEmpty()) {
        return Lists.newArrayList();
      }

      String[] queries = search.toLowerCase().split(" ");
      List<FinderEnviron> searchedList = Lists.newArrayList();
      for(FinderEnviron environ : getEnvirons()) {
        if(queryMatches(searchedList, environ, query)) {
          searchedList.add(environ);
          continue;
        }

        if(queries.length != 1) {
          boolean matches = true;
          for(String advancedQuery : queries) {
            if(!queryMatches(searchedList, environ, advancedQuery)) {
              matches = false;
              break;
            }
          }

          if(matches) {
            searchedList.add(environ);
          }
        }
      }

      return sortBy(sortBy, searchedList);
    }

    private boolean queryMatches(List<FinderEnviron> list, FinderEnviron environ, String query) {
      if(matchQuery(environ.getName(), query) || matchQuery(environ.getShortDescription(), query)) {
        return true;
      }

      String authorName = environ.getAuthor();
      return (authorName != null && matchQuery(authorName, query));
    }

    public List<FinderEnviron> tag(FinderSortBy sortBy, FinderTag tag) {
      return tag(sortBy, IntSet.of(tag.getId()), true);
    }

    public List<FinderEnviron> tag(FinderSortBy sortBy, IntSet finderTags) {
      return tag(sortBy, finderTags, false);
    }

    private List<FinderEnviron> tag(FinderSortBy sortBy, IntSet finderTags, boolean checkForParent) {
      List<FinderEnviron> filteredList = Lists.newArrayList();
      if(finderTags.isEmpty()) {
        return filteredList;
      }

      for(FinderEnviron environ : getEnvirons()) {
        for(FinderTag tag : environ.getTags()) {
          if((checkForParent && finderTags.contains(tag.getParentCategory())) || finderTags.contains(tag.getId())) {
            if(!filteredList.contains(environ)) {
              filteredList.add(environ);
            }
          }
        }
      }

      return sortBy(sortBy, filteredList);
    }

    public List<FinderEnviron> getEnvirons() {
      for(int i = 0; i < this.index.getIndexEnvirons().size(); i++) {
        FinderEnviron environ = this.index.getIndexEnvirons().get(i);

        if(environ instanceof FinderIndex.FinderIndexEnviron) {
          Optional<FinderEnviron> cachedEnviron = this.index.finderController.getCachedEnvirons(environ.getNamespace());

          if(cachedEnviron.isPresent()) {
            FinderEnviron cachedEnvironValue = cachedEnviron.get();
            cachedEnvironValue.setRanking(environ.getRanking());
            this.index.getIndexEnvirons().remove(environ);
            this.index.getIndexEnvirons().add(i, cachedEnvironValue);
          }
        }
      }

      return this.index.getIndexEnvirons();
    }

    public List<FinderEnviron> sortBy(FinderSortBy sortBy) {
      return sortBy(sortBy, null);
    }

    private List<FinderEnviron> sortBy(FinderSortBy sortBy, List<FinderEnviron> environs) {
      List<FinderEnviron> sortedList = (environs == null) ? Lists.newArrayList(getEnvirons()) : environs;

      switch(sortBy) {
        case NAME_AZ:
          sortedList.sort(Comparator.comparing(FinderEnviron::getName));
          break;
        case NAME_ZA:
          sortedList.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
          break;
        case TRENDING:
          sortedList.sort(Comparator.comparingInt(FinderEnviron::getRanking));
          break;
        case DOWNLOADS:
          sortedList.sort((o1, o2) -> Integer.compare(o2.getDownloads(), o1.getDownloads()));
          break;
        case RATING:
          sortedList.sort((o1, o2) -> {
            FinderEnviron.Rating first = o1.getRating();
            FinderEnviron.Rating second = o2.getRating();
            return Double.compare(second.getRating() * second.getCount(), first.getRating() * first.getCount());
          });
          break;
        case LATEST:
          sortedList.sort(((o1, o2) -> Long.compare(o2.getLastUpdate(), o1.getLastUpdate())));
          break;
        case OLDEST:
          sortedList.sort(Comparator.comparingLong(FinderEnviron::getLastUpdate));
          break;
      }

      return sortedList;
    }

    boolean matchQuery(String s1, String s2) {
      return s1.toLowerCase().replace(" ", "").contains(s2);
    }
  }

}
