package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuGroup;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.AbstractMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.RootMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.environ.Environ;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.AbstractSidebarActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.playeraccessories.childs.MilieuContentActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.activity.milieus.CategoryWidget;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.HrWidget;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import net.labymod.api.util.CharSequences;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

@AutoActivity
@Link("activity/milieus.lss")
public class MilieuActivity extends AbstractSidebarActivity {

  private final AbstractMilieuRegistry registry;

  private Milieu defaultMilieu;

  @Nullable
  private Milieu selectedMilieu;
  private Milieu previousMilieu;

  private Widget temporaryMilieuWidget;

  private Milieu lastFilter;

  public MilieuActivity(AbstractMilieuRegistry registry) {
    this.registry = registry;
    updateScreen();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    if(screenRendererWidget.getScreen() == null) updateScreen();
  }

  @Override
  public void onCategoryListInitialize(VerticalListWidget<Widget> categoryList) {
    boolean searching = !searchField.getText().trim().isEmpty();

    List<Environ> environCategories = new ArrayList<>();
    registry.forEach((category) -> {
      if(category instanceof RootMilieuRegistry && ((RootMilieuRegistry) category).isEnviron()) {
        String namespace = ((RootMilieuRegistry) category).getNamespace();

        PlayerAccessories playerAccessories = PlayerAccessories.instance();

        playerAccessories.environService().getEnviron(namespace).ifPresent(environCategories::add);
        return;
      }

      if(this.defaultMilieu == null) this.defaultMilieu = category;

      if(this.selectedMilieu == null) {
        this.selectedMilieu = category;
        this.previousMilieu = category;

        updateScreen();
      }

      categoryList.addChild(createCategory(category, searching));
    });

    if(environCategories.isEmpty()) return;

    HrWidget environHr = new HrWidget();
    environHr.addId("environ");

    categoryList.addChild(environHr);
    categoryList.addChild(
        ComponentWidget.i18n("player-accessories.ui.milieus.environs.name").addId("category-sub"));
  }

  @Override
  protected void initializeSidebarFooter(DivWidget widget) {
    super.initializeSidebarFooter(widget);
  }

  @Override
  public void onSearchUpdateListener(String searchContent) {
    if(updateScreen()) reload();
  }

  @Override
  public void onCloseScreen() {
    super.onCloseScreen();
    Laby.fireEvent(new ConfigurationSaveEvent());
  }

  public void resetSearch() {
    if(!(selectedMilieu instanceof MilieuGroup)) {
      selectedMilieu = (selectedMilieu == null) ? defaultMilieu : selectedMilieu;
      displayScreen(selectedMilieu.createActivityLazy());

      return;
    }

    Milieu previousMilieu = this.previousMilieu;
    if(previousMilieu instanceof MilieuGroup) {
      selectedMilieu = defaultMilieu;
      displayScreen(selectedMilieu.createActivityLazy());

      return;
    }

    selectedMilieu = previousMilieu;
    displayScreen(previousMilieu.createActivityLazy());
  }

  private boolean updateScreen() {
    String text = searchField.getText().trim();

    if(text.isEmpty()) {
      searchField.setEditable(true);

      if(selectedMilieu == null) return true;
      if(temporaryMilieuWidget != null) {
        screenRendererWidget.removeChild(temporaryMilieuWidget);
        temporaryMilieuWidget = null;
      }

      resetSearch();
      return true;
    }

    if(text.length() < 2) {
      displayScreen((Activity) null);
      if(temporaryMilieuWidget != null) screenRendererWidget.removeChild(temporaryMilieuWidget);

      temporaryMilieuWidget = createEmptyMilieu(text, "tooShort");
      screenRendererWidget.addChildInitialized(temporaryMilieuWidget);

      return true;
    }

    List<Milieu> milieus = registry.collect((milieu) -> filterMilieus(text, milieu));
    if(milieus.isEmpty()) {
      displayScreen((Activity) null);
      if(temporaryMilieuWidget != null) screenRendererWidget.removeChild(temporaryMilieuWidget);

      temporaryMilieuWidget = createEmptyMilieu(text, "noResults");
      screenRendererWidget.addChildInitialized(temporaryMilieuWidget);

      return false;
    }

    if(temporaryMilieuWidget != null) {
      screenRendererWidget.removeChild(temporaryMilieuWidget);
      temporaryMilieuWidget = null;
    }

    MilieuGroup group = MilieuGroup.named(Component.text(text)).of(milieus).filtered(true);
    if(!(selectedMilieu instanceof MilieuGroup)) previousMilieu = selectedMilieu;

    selectedMilieu = group;
    MilieuContentActivity activity = group.createActivityLazy();
    activity.screenCallback((milieu) -> {
      if(milieu == group) {
        lastFilter = null;
        searchField.setEditable(true);

        return group;
      }

      Milieu parent = milieu.parent();
      if(isPlayerAccessoriesRootSetting(parent) && lastFilter != null && !isPlayerAccessoriesRootSetting(lastFilter.parent())) {
        lastFilter = null;

        searchField.setEditable(true);
        return group;
      }

      if(lastFilter == null && !isPlayerAccessoriesRootSetting(milieu)) {
        lastFilter = milieu;
        searchField.setEditable(false);
      } else if(lastFilter != null && milieu == lastFilter.parent()) {
        lastFilter = null;
        searchField.setEditable(true);

        return group;
      }

      searchField.setEditable(false);
      return milieu;
    });

    displayScreen(activity);
    return true;
  }

  private boolean isPlayerAccessoriesRootSetting(Milieu milieu) {
    if(!(milieu instanceof RootMilieuRegistry rootMilieuRegistry)) return false;
    return rootMilieuRegistry.getNamespace().equals("player-accessories");
  }

  private boolean filterMilieus(String searchTerm, Milieu milieu) {
    if(!milieu.isElement()) return false;
    if(CharSequences.containsLowercase(I18n.translate(milieu.getTranslationKey() + ".name"), searchTerm)) return true;

    for(String searchTag : milieu.getSearchTags()) {
      if(CharSequences.containsLowercase(searchTag, searchTerm)) return true;
    }

    return false;
  }

  private CategoryWidget createCategory(Milieu milieu, boolean searching) {
    CategoryWidget category = new CategoryWidget(milieu);

    category.setEnabled((selectedMilieu != milieu || searching));
    category.setActive((selectedMilieu == milieu && !searching));
    category.setPressable(() -> {
      setSelectedMilieu(milieu);
      reload();
    });

    return category;
  }

  public void setSelectedMilieu(Milieu milieu) {
    selectedMilieu = milieu;
    lastFilter = null;

    searchField.setEditable(true);
    searchField.setText("");
    searchField.setFocused(false);

    updateScreen();
  }

  private Widget createEmptyMilieu(String query, String translationKey) {
    FlexibleContentWidget content = new FlexibleContentWidget();
    content.addId("content");

    HorizontalListWidget header = new HorizontalListWidget();
    header.addId("header-milieu");

    ComponentWidget title = ComponentWidget.text(query);
    title.addId("title");

    header.addEntry(title);
    content.addContent(header);

    DivWidget infoWrapper = new DivWidget();
    infoWrapper.addId("wrapper-info");

    ComponentWidget info = ComponentWidget.component(
        Component.translatable("player-accessories.ui.milieus.search." + translationKey)
    );

    info.addId("info");

    infoWrapper.addChild(info);

    content.addFlexibleContent(infoWrapper);

    return content;
  }

}
