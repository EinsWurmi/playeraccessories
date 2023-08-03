package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.HrWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.ScreenRendererWidget;

@Link("activity/sidebar-activity.lss")
public abstract class AbstractSidebarActivity extends Activity {

  private final ListSession<Widget> listSession = new ListSession<>();

  protected final ScreenRendererWidget screenRendererWidget;
  protected final TextFieldWidget searchField;

  public AbstractSidebarActivity() {
    this.screenRendererWidget = new ScreenRendererWidget();
    this.screenRendererWidget.addId("screen-renderer");

    this.searchField = new TextFieldWidget();
    this.searchField.addId("search-field-input");
    this.searchField.placeholder(Component.translatable("player-accessories.ui.textfield.search"));
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("container");

    FlexibleContentWidget sidebarContainer = new FlexibleContentWidget();
    sidebarContainer.addId("container-sidebar");

    VerticalListWidget<Widget> sidebar = new VerticalListWidget<>(listSession);
    sidebar.addId("sidebar");

    searchField.updateListener((searchContent) -> {
      searchContent = searchContent.trim();
      onSearchUpdateListener(searchContent);
    });

    sidebar.addChild(searchField);
    sidebar.addChild(new HrWidget());

    VerticalListWidget<Widget> categoryList = new VerticalListWidget<>();
    categoryList.addId("list-categories");

    onCategoryListInitialize(categoryList);
    sidebar.addChild(categoryList);

    ScrollWidget scrollWidget = new ScrollWidget(sidebar);

    DivWidget scrollContainer = new DivWidget();
    scrollContainer.addId("container-scroll");
    scrollContainer.addChild(scrollWidget);

    sidebarContainer.addFlexibleContent(scrollContainer);

    DivWidget sidebarFooter = new DivWidget();
    sidebarFooter.addId("footer-sidebar");

    initializeSidebarFooter(sidebarFooter);
    if(!sidebarFooter.getChildren().isEmpty()) sidebarContainer.addContent(sidebarFooter);

    container.addContent(sidebarContainer);
    container.addFlexibleContent(screenRendererWidget);

    this.document().addChild(container);
  }

  protected void initializeSidebarFooter(DivWidget widget) { }

  protected void displayScreen(Activity activity) {
    if(screenRendererWidget.getScreen() == activity) return;
    screenRendererWidget.displayScreen(activity);
  }

  public abstract void onCategoryListInitialize(VerticalListWidget<Widget> categoryList);
  public abstract void onSearchUpdateListener(String searchContent);

}
