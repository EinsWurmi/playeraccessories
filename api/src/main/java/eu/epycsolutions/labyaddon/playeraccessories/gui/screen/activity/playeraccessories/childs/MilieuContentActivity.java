package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.playeraccessories.childs;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuGroup;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuHeader;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.list.ListMilieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.utils.MilieuActivitySupplier;
import eu.epycsolutions.labyaddon.playeraccessories.events.widget.MilieuWidgetInitializeEvent;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widgets.MilieuHeaderWidget;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widgets.MilieuWidget;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.HorizontalAlignment;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.ParentScreen;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.Links;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.ScreenRendererWidget;
import net.labymod.api.event.Priority;
import net.labymod.api.util.KeyValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@AutoActivity
@Links({ @Link("activity/tabbed.lss"), @Link(value = "activity/milieus.lss", priority = Priority.EARLY) })
public class MilieuContentActivity extends Activity {

  private final Milieu originMilieuHolder;
  private Milieu currentMilieuHolder;

  private Map<String, ListSession<?>> sessions;
  private Function<Milieu, Milieu> screenCallback;
  private Runnable closeHandler;

  private HeaderType headerType;
  private final boolean lazy;

  public MilieuContentActivity(Milieu holder) { this(holder, false); }

  public MilieuContentActivity(Milieu holder, boolean lazy) {
    if(!holder.isInitialized()) throw new IllegalStateException("Milieu is not initialized yet.");

    this.originMilieuHolder = holder;
    this.sessions = new HashMap<>();
    this.currentMilieuHolder = holder;
    this.headerType = HeaderType.FIXED;

    this.lazy = lazy;
  }

  public MilieuContentActivity screenCallback(Consumer<Milieu> screenCallback) {
    this.screenCallback = ((milieu) -> {
      screenCallback.accept(milieu);
      return milieu;
    });

    return this;
  }

  public MilieuContentActivity screenCallback(Function<Milieu, Milieu> screenCallback) {
    this.screenCallback = screenCallback;
    return this;
  }

  public MilieuContentActivity closeHandler(Runnable closeHandler) {
    this.closeHandler = closeHandler;
    return this;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("content", ("header-" + this.headerType.name().toLowerCase()));

    HorizontalListWidget header = new HorizontalListWidget();
    if(this.headerType != HeaderType.NONE) {
      header = new HorizontalListWidget();
      header.addId("milieu-header");

      boolean hasParent = (this.currentMilieuHolder.hasParent() && currentMilieuHolder.parent().isHoldable());
      if(this.closeHandler != null || hasParent || (!isOriginHolder() && isOriginFiltered())) {
        ButtonWidget backButton = ButtonWidget.icon(Icon.sprite8(SpriteCommon.TEXTURE, 4, 4));
        backButton.addId("button-back");
        if(this.currentMilieuHolder == this.originMilieuHolder) backButton.addId("back-button-origin");
        backButton.setPressable(() -> {
          if(hasParent) this.currentMilieuHolder = this.currentMilieuHolder.parent();
          else {
            this.currentMilieuHolder = this.originMilieuHolder;
            if(this.closeHandler != null) this.closeHandler.run();
          }

          if(this.screenCallback != null) this.currentMilieuHolder = this.screenCallback.apply(this.currentMilieuHolder);
          if(this.currentMilieuHolder != null) reload();
        });

        header.addEntry(backButton);
      }

      Icon icon = this.currentMilieuHolder.getIcon();
      if(icon != null) {
        IconWidget iconWidget = new IconWidget(icon);
        iconWidget.addId("parent-icon");
        header.addEntry(iconWidget);
      }

      Component title = this.currentMilieuHolder.displayName();
      if(title != null) {
        ComponentWidget titleWidget = ComponentWidget.component(title);
        titleWidget.addId("title");
        header.addEntry(titleWidget);
      }

      if(this.currentMilieuHolder instanceof ListMilieu) {
        ButtonWidget addButton = ButtonWidget.icon(Icon.sprite8(SpriteCommon.TEXTURE, 3, 5));
        addButton.addId("button-id");
        addButton.setPressable(() -> {
          ListMilieu list = (ListMilieu) this.currentMilieuHolder;
          this.currentMilieuHolder = list.createNew();

          if(this.screenCallback != null) this.currentMilieuHolder = this.screenCallback.apply(this.currentMilieuHolder);
          if(this.currentMilieuHolder != null) reload();
        });

        header.addEntry(addButton).alignment().set(HorizontalAlignment.RIGHT);
      }

      if(
          this.headerType == HeaderType.FIXED ||
          (
              this.headerType == HeaderType.FIXED_IN_CHILDREN &&
              this.currentMilieuHolder.hasParent() &&
              (this.currentMilieuHolder.parent().isHoldable() || this.currentMilieuHolder.parent() instanceof ListMilieu)
          )
      ) container.addContent(header);
    }

    ListSession<?> session = this.sessions.computeIfAbsent(this.currentMilieuHolder.getPath(), (function) -> new ListSession<>());
    if(this.currentMilieuHolder.isElement()) {
      MilieuElement milieuElement = this.currentMilieuHolder.asElement();
      MilieuHandler handler = milieuElement.handler();

      Activity activity = null;

      if(milieuElement.getWidgets() != null && milieuElement.getWidgets()[0] instanceof MilieuActivitySupplier) {
        activity = ((MilieuActivitySupplier) milieuElement.getWidgets()[0]).activity(milieuElement);
      } else if(handler != null && handler.opensActivity(milieuElement)) {
        activity = handler.activity(milieuElement);
      }

      if(activity != null) {
        ScreenRendererWidget screenRendererWidget = new ScreenRendererWidget();
        screenRendererWidget.addId("environ-activity-renderer");

        screenRendererWidget.displayScreen(activity);

        container.addFlexibleContent(screenRendererWidget);
        this.document().addChild(container);
      }

      return;
    }

    VerticalListWidget<Widget> milieuList = new VerticalListWidget<>();
    milieuList.addId("list");

    if(
        this.headerType == HeaderType.FIXED ||
        (
            this.headerType == HeaderType.FIXED_IN_CHILDREN &&
            this.currentMilieuHolder.hasParent() &&
            (this.currentMilieuHolder.parent().isHoldable() || this.currentMilieuHolder.parent() instanceof ListMilieu)
        )
    ) milieuList.addChild(header);

    List<Widget> list = new ArrayList<>();
    Milieu parentMilieu = this.currentMilieuHolder;

    for(KeyValue<Milieu> element : this.currentMilieuHolder.getElements()) {
      Milieu milieu = element.getValue();

      if(milieu.isElement() && !milieu.hasAdvancedButton()) {
        MilieuElement milieuElement = milieu.asElement();
        if(!milieuElement.hasControlButton() && milieuElement.getWidgets() == null) continue;
      }

      if(milieu instanceof MilieuHeader) {
        MilieuHeader milieuHeader = (MilieuHeader) milieu;
        List<Component> rows = milieuHeader.getRows();

        for(int i = 0; i < rows.size(); i++) {
          Component row = rows.get(i);
          MilieuHeaderWidget milieuHeaderWidget = new MilieuHeaderWidget(row);

          if(rows.size() > 1) {
            if(i == 0) milieuHeaderWidget.addId("header-first");
            if(i == rows.size() - 1) milieuHeaderWidget.addId("header-last");
          } else {
            milieuHeaderWidget.addId("header-single");
          }

          list.add(milieuHeaderWidget.addId("header-" + (milieuHeader.isCenter() ? "center" : "left")));
        }

        continue;
      }

      list.add(new MilieuWidget(milieu, lazy, (layer) -> {
        this.currentMilieuHolder = (layer != null) ? layer : parentMilieu;

        if(this.screenCallback != null) this.currentMilieuHolder = this.screenCallback.apply(this.currentMilieuHolder);
        if(this.currentMilieuHolder != null) reload();
      }));
    }

    labyAPI.eventBus().fire(new MilieuWidgetInitializeEvent((ParentScreen) getParent(), this.currentMilieuHolder, list));

    for(Widget milieuWidget : list) milieuList.addChild(milieuWidget);

    ScrollWidget scrollWidget = new ScrollWidget(milieuList, session);
    scrollWidget.addId("scroll");

    if(this.headerType == HeaderType.FIXED) scrollWidget.addId("header-spacing");
    container.addFlexibleContent(scrollWidget);

    this.document().addChild(container);
  }

  @Override
  public boolean displayPreviousScreen() {
    return false;
  }

  public void setHeaderType(HeaderType headerType) {
    this.headerType = headerType;
  }

  public void setSessions(Map<String, ListSession<?>> sessions) {
    this.sessions = sessions;
  }

  public void setInitializedHeader(boolean header) {
    this.headerType = header ? this.headerType : HeaderType.NONE;
  }

  private boolean isOriginHolder() {
    return this.currentMilieuHolder == this.originMilieuHolder;
  }

  private Milieu getCurrentHolder() {
    return this.currentMilieuHolder;
  }

  private Milieu getOriginHolder() {
    return this.originMilieuHolder;
  }

  private boolean isOriginFiltered() {
    return (this.originMilieuHolder instanceof MilieuGroup && ((MilieuGroup) this.originMilieuHolder).isFiltered());
  }


  public enum HeaderType {
    NONE,
    SCROLL,
    FIXED,
    SCROLL_IN_CHILDREN,
    FIXED_IN_CHILDREN;
  }

}
