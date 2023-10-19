package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.environs;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import eu.epycsolutions.labyaddon.playeraccessories.environ.EnvironService;
import eu.epycsolutions.labyaddon.playeraccessories.environ.LoadedEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderController;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.finder.InstalledItemWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.Links;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import java.util.Optional;

@AutoActivity
@Links({ @Link("activity/environs/installed.lss"), @Link("activity/environs/environ-item.lss") })
public class MyEnvironsActivity extends Activity {

  private final ListSession<InstalledItemWidget> session = new ListSession<>();

  private final FinderController finderController;
  private final EnvironService environService;

  private String searchQuery = "";

  public MyEnvironsActivity(FinderController finderController) {
    this.finderController = finderController;

    this.environService = PlayerAccessories.instance().environService();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
  }

  private FinderEnviron getFinderEnviron(LoadedEnviron loadedEnviron) {
    if(!loadedEnviron.info().isFinderEnviron()) {
      return new DummyEnviron(loadedEnviron);
    }

    Optional<FinderEnviron> optionalEnviron = this.finderController.getFinderIndex().filter().namespace(loadedEnviron.info().getNamespace());
    return optionalEnviron.orElseGet(() -> new DummyEnviron(loadedEnviron));
  }

  public String getSearchQuery() {
    return this.searchQuery;
  }

  public void search(String query) {
    this.searchQuery = query;
    reload();
  }

  public class DummyEnviron extends FinderEnviron {
    private DummyEnviron(LoadedEnviron loadedEnviron) {
      this.namespace = loadedEnviron.info().getNamespace();

      this.author = loadedEnviron.info().getAuthor();

      this.name = loadedEnviron.info().getDisplayName();
      this.shortDescription = loadedEnviron.info().getDescription();
    }
  }

}
