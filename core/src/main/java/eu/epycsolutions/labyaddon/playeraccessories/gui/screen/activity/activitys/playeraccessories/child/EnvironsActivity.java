package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import eu.epycsolutions.labyaddon.playeraccessories.environ.FinderSortBy;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderController;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.environs.EnvironProfileActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.environs.FinderActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.environs.MyEnvironsActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.ScreenInstance;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.Links;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.renderer.DefaultEntryRenderer;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.ScreenRendererWidget;
import org.jetbrains.annotations.Nullable;

@AutoActivity
@Links({ @Link("activity/environs/environs.lss"), @Link("activity/sidebar-activity.lss") })
public class EnvironsActivity extends Activity {

  private final FinderController finderController;

  private final FinderActivity finderActivity;
  private final MyEnvironsActivity myEnvironsActivity;

  private final ScreenRendererWidget screenRendererWidget;

  private final TextFieldWidget searchField;
  private final DropdownWidget<FinderSortBy> sortDropdownWidget;

  @Nullable
  private ButtonWidget trendingButton;
  @Nullable
  private VerticalListWidget<Widget> categoryList;

  private boolean pseudoReload;

  public EnvironsActivity() {
    this.finderController = PlayerAccessories.references().finderController();

    this.finderActivity = new FinderActivity(this.finderController);
    this.myEnvironsActivity = new MyEnvironsActivity(this.finderController);

    this.screenRendererWidget = new ScreenRendererWidget();
    this.screenRendererWidget.addId("screen-renderer");
    this.screenRendererWidget.setPreviousScreenHandler((screen) -> screen instanceof EnvironProfileActivity);
    this.screenRendererWidget.displayScreen(this.finderActivity);
    this.screenRendererWidget.addDisplayListener((screen) -> reload());

    this.searchField = new TextFieldWidget();
    this.searchField.addId("search-field-input");
    this.searchField.placeholder(Component.translatable("player-accessories.ui.textfield.search"));
    this.searchField.updateListener((text) -> {
      ScreenInstance screen = this.screenRendererWidget.getScreen();

      if(screen instanceof MyEnvironsActivity) {
        this.myEnvironsActivity.search(text);

        if(this.pseudoReload) {
          this.pseudoReload = false;
        }

        return;
      }

      boolean reload = false;

      if(text.isEmpty()) {
        this.finderActivity.trending();
        reload = true;
      } else if(!this.finderActivity.isSearch(text)) {
        this.finderActivity.search(text);
        reload = true;
      }

      if(reload && !(screen instanceof EnvironProfileActivity)) {
        if(this.pseudoReload) {
          this.pseudoReload = false;
        } else {
          reload();
        }
      }

      if(screen instanceof EnvironProfileActivity) {
        this.screenRendererWidget.displayScreen(this.finderActivity);
      }
    });

    this.sortDropdownWidget = new DropdownWidget<>();
    this.sortDropdownWidget.setChangeListener((selected) -> {
      this.finderActivity.setSortBy(selected);
      reload();
    });

    DefaultEntryRenderer<?> renderer = (DefaultEntryRenderer<?>) this.sortDropdownWidget.entryRenderer();
    renderer.setTranslationKeyPrefix("player-accessories.environs.finder.filter");

    this.sortDropdownWidget.addId("input-widget");
    this.sortDropdownWidget.addAll(FinderSortBy.values());
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    this.document.getChildren().clear();

    String query;
    String text = this.searchField.getText();

    if(this.screenRendererWidget.getScreen() instanceof MyEnvironsActivity) {
      query = this.myEnvironsActivity.getSearchQuery();
    } else {
      query = this.finderActivity.getSearchQuery();
    }

    if(!text.equals(query)) {
      this.pseudoReload = true;
      this.searchField.setText(query);
    }

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("container");

    FlexibleContentWidget header = new FlexibleContentWidget();
    header.addId("header");

    addTabWidget(header);


    ButtonWidget storeButton = ButtonWidget.component(getScreenName(this.finderActivity, false));
    storeButton.addId("button-store");

    setButtonEnabled(storeButton, this.finderActivity);
    storeButton.setPressable(() -> openScreen(this.finderActivity));


    ButtonWidget installedButton = ButtonWidget.component(getScreenName(this.myEnvironsActivity, false));
    installedButton.addId("button-installed");

    setButtonEnabled(installedButton, this.myEnvironsActivity);
    installedButton.setPressable(() -> openScreen(this.myEnvironsActivity));

    header.addContent(storeButton);
    header.addContent(installedButton);

    container.addContent(header);
    container.addFlexibleContent(this.screenRendererWidget);

    this.document.addChild(container);
  }

  @Override
  public void onCloseScreen() {
    super.onCloseScreen();
  }

  private void addTabWidget(FlexibleContentWidget widget) {
    ScreenInstance screen = this.screenRendererWidget.getScreen();

    if(screen instanceof MyEnvironsActivity) {
      ComponentWidget component = ComponentWidget.component(getScreenName(screen, true));
      component.addId("title");

      widget.addContent(component);
      widget.addFlexibleContent(this.searchField);

      return;
    }

    widget.addContent(this.sortDropdownWidget);
    widget.addFlexibleContent(this.searchField);
  }

  private void setButtonEnabled(ButtonWidget widget, ScreenInstance screenInstance) {
    ScreenInstance screen = this.screenRendererWidget.getScreen();

    boolean enabled = (screen != screenInstance);
    if(screen instanceof FinderActivity && screen instanceof EnvironProfileActivity) {
      enabled = false;
    }

    widget.setEnabled(enabled);
    widget.setActive(!enabled);
  }

  private boolean openScreen(ScreenInstance screenInstance) {
    ScreenInstance screen = this.screenRendererWidget.getScreen();
    if(screen != screenInstance) return false;

    this.screenRendererWidget.displayScreen(screenInstance);
    return true;
  }

  private Component getScreenName(ScreenInstance screenInstance, boolean title) {
    String key = title ? "title" : "name";

    if(screenInstance instanceof MyEnvironsActivity) {
      return Component.translatable("player-accessories.finder.category.myEnvirons." + key);
    }

    return Component.translatable("player-accessories.finder.category.store." + key);
  }

}
