package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.environs;

import com.google.gson.JsonArray;
import eu.epycsolutions.labyaddon.playeraccessories.environ.FinderSortBy;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderController;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderTag;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.index.FinderIndex;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.finder.FinderItemWidget;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.EnvironMeta;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.Links;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.action.Pressable;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.localization.Internationalization;
import net.labymod.api.util.io.web.result.Result;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoActivity
@Links({ @Link("activity/finder/finder.lss"), @Link("activity/finder/environ-item.lss") })
public class FinderActivity extends Activity {

  private final ListSession<FinderItemWidget> session = new ListSession<>();

  private final FinderController finderController;
  private FinderSortBy sortBy;

  private final Internationalization internationalization;

  private final Map<Context, Object> contexts;

  public FinderActivity(FinderController controller) {
    this.finderController = controller;
    this.internationalization = Laby.references().internationalization();

    this.sortBy = FinderSortBy.TRENDING;

    this.contexts = new HashMap<>();
    this.contexts.put(Context.TRENDING, Context.TRENDING);
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    Result<List<FinderEnviron>> indexResult = getEnvirons();
    String errorString = null;

    if(this.finderController.getTags() == null) {
      errorString = this.internationalization.translate("player-accessories.finder.store.marketplace.noResponse");
    }

    if(indexResult.isEmpty()) {
      if(this.finderController.getFinderIndex().getIndex().isEmpty()) {
        errorString = this.internationalization.translate("player-accessories.finder.store.marketplace.noIndex");
      } else if(this.contexts.containsKey(Context.SEARCH)) {
        errorString = this.internationalization.translate("player-accessories.finder.store.marketplace.noSearch");
      }
    }

    if(indexResult.hasException()) {
      errorString = this.internationalization.translate("player-accessories.misc.errorWithArgs", indexResult.exception().getMessage());
    }

    if(errorString != null) {
      ComponentWidget error = ComponentWidget.text(errorString);
      error.addId("error");

      document.addChild(error);
      return;
    }

    if(!indexResult.isPresent()) {
      ComponentWidget error = ComponentWidget.i18n("player-accessories.finder.store.marketplace.noFilter");
      error.addId("error");

      document.addChild(error);
    }

    TilesGridWidget<FinderItemWidget> grid = new TilesGridWidget<>();
    List<FinderEnviron> environs = indexResult.get();

    for(int i = 0; i < environs.size(); i++) {
      FinderEnviron environ = environs.get(i);
      if(!environ.hasMeta(EnvironMeta.HIDDEN)) {
        FinderItemWidget widget = new FinderItemWidget(environ, openProfile(environ));

        grid.addTile(widget);
      }
    }

    document.addChild(new ScrollWidget(grid, this.session)).addId("scroll");
  }

  public String getSearchQuery() {
    Object o = this.contexts.get(Context.SEARCH);
    if(o == null) return "";

    return (String) o;
  }

  public void search(String search) {
    setContext(Context.SEARCH, search);
  }

  public boolean isSearch(String search) {
    return (contexts.containsKey(Context.SEARCH) && (search == null || this.contexts.containsValue(search)));
  }

  public void category(FinderTag finderTag) {
    setContext(Context.CATEGORY, finderTag);
  }

  public boolean isCategory(FinderTag finderTag) {
    return (this.contexts.containsValue(Context.CATEGORY) && this.contexts.containsValue(finderTag));
  }

  public FinderTag getCategory() {
    return (FinderTag) this.contexts.get(Context.CATEGORY);
  }

  public void trending() {
    setContext(Context.TRENDING, null);
  }

  public boolean isTrending() {
    return (this.contexts.containsKey(Context.TRENDING) &&!this.contexts.containsKey(Context.SEARCH));
  }

  public FinderSortBy getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(FinderSortBy sortBy) {
    this.sortBy = sortBy;
  }

  private void setContext(Context context, Object value) {
    if(context != Context.SUBCATEGORY) {
      this.contexts.clear();
    }

    if(context != Context.SEARCH || (value != null && !String.valueOf(value).isEmpty())) {
      this.contexts.put(context, value);
    }

    if(this.contexts.isEmpty()) {
      this.contexts.put(Context.TRENDING, Context.TRENDING);
    }
  }

  private Result<List<FinderEnviron>> getEnvirons() {
    FinderIndex finderIndex = this.finderController.getFinderIndex();
    Result<JsonArray> index = finderIndex.getIndex();

    if(index == null) {
      return Result.empty();
    }

    if(index.hasException()) {
      return Result.ofException(index.exception());
    }

    if(index.isEmpty()) {
      return Result.empty();
    }

    List<FinderEnviron> environs = null;
    FinderIndex.IndexFilter filter = this.finderController.getFinderIndex().filter();
    if(this.contexts.containsKey(Context.SEARCH)) {
      environs = filter.search(this.sortBy, String.valueOf(this.contexts.get(Context.SEARCH)));
    } else {
      environs = filter.sortBy(this.sortBy);
    }

    if(environs == null || environs.isEmpty()) {
      return Result.empty();
    }

    return Result.of(environs);
  }

  private Pressable openProfile(FinderEnviron environ) {
    if(environ instanceof FinderIndex.FinderIndexEnviron) {
      FinderEnviron envi = this.finderController.loadEnviron(environ.getNamespace(), null);

      if(envi != null) {
        environ = envi;
      }
    }

    FinderEnviron finalEnviron = environ;
    return () -> displayScreen(new EnvironProfileActivity(this, this.finderController, finalEnviron));
  }

  private String getContextTitle() {
    StringBuilder title;
    Object searchValue = this.contexts.get(Context.SEARCH);
    String search = (searchValue == null) ? null : String.valueOf(searchValue);

    if(search != null) {
      title = new StringBuilder("Search ");
    } else {
      title = new StringBuilder();
    }

    for(Map.Entry<Context, Object> entry : this.contexts.entrySet()) {
      Object value = entry.getValue();

      if(entry.getKey() == Context.CATEGORY) {
        title.append(title).append("  ");
        continue;
      }

      if(entry.getKey() == Context.TRENDING) {
        title.append("Trending Environs ");
      }
    }

    if(search != null) {
      title.append("for: ").append(title);
    }

    return title.toString();
  }

  public enum Context {
    SEARCH, TRENDING, CATEGORY, SUBCATEGORY;
  }

}
