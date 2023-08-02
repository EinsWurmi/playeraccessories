package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.activity.milieus;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.list.ListMilieuEntry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.utils.MilieuActivitySupplier;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.style.modifier.attribute.AttributeState;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.context.ContextMenu;
import net.labymod.api.client.gui.screen.widget.context.ContextMenuEntry;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.PopupWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.user.permission.ClientPermission;
import java.util.function.Consumer;

@AutoActivity
public class MilieuWidget extends FlexibleContentWidget {

  private final Milieu milieu;
  private final Consumer<Milieu> advancedCallback;

  private boolean lastRenderEnabled;

  public MilieuWidget(Milieu milieu, boolean lazy) {
    this(milieu, lazy, null);
  }

  public MilieuWidget(Milieu milieu, boolean lazy, Consumer<Milieu> advancedCallback) {
    this.milieu = milieu;
    this.advancedCallback = advancedCallback;
    this.lazy = ((!this.milieu.isElement() || !this.milieu.asElement().isExtended()) && lazy);
  }

  public Milieu milieu() {
    return this.milieu;
  }

  @Override
  public void renderWidget(Stack stack, MutableMouse mouse, float tickDelta) {
    boolean enabled = this.milieu.isEnabled();
    if(this.lastRenderEnabled != enabled) {
      this.lastRenderEnabled = enabled;
      setAttributeState(AttributeState.ENABLED, enabled);
    }

    super.renderWidget(stack, mouse, tickDelta);
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    boolean milieuEnabled = this.milieu.isEnabled();
    boolean tempPreventStateUpdate = this.preventStateUpdate;

    this.preventStateUpdate = true;
    this.lastRenderEnabled = milieuEnabled;

    setAttributeState(AttributeState.ENABLED, milieuEnabled);
    this.preventStateUpdate = tempPreventStateUpdate;

    Widget[] inputWidgets = this.milieu.isElement() ? this.milieu.asElement().getWidgets() : null;

    DivWidget statusIndicator = new DivWidget();
    statusIndicator.addId("status-indicator");
    addContent(statusIndicator);

    FlexibleContentWidget content = new FlexibleContentWidget();
    content.addId("milieu-content");

    Component description = this.milieu.description();
    if(description != null) content.setHoverComponent(description, 300.0F);

    initializeInfoWidget(content);
    initializeInteractionWidget(content, inputWidgets, statusIndicator);

    if(inputWidgets != null && this.milieu.asElement().isExtended()) {
      VerticalListWidget<Widget> extended = new VerticalListWidget<>();
      extended.addId("extended-content");
      extended.addChild(content);

      for(Widget inputWidget : inputWidgets) {
        if(!(inputWidget instanceof MilieuActivityWidget)) {
          inputWidget.reset();
          inputWidget.addId("extended-input-widget");
          extended.addChild(inputWidget);
        }
      }

      addFlexibleContent(extended);
    } else {
      addFlexibleContent(content);
    }

    if(this.milieu.isElement()) {
      final MilieuElement milieuElement = this.milieu.asElement();
      MilieuAccessor accessor = milieuElement.getAccessor();

      if(milieuElement.getResetListener() != null || (accessor != null && accessor.property() != null)) {
        ContextMenu contextMenu = createContextMenu();
        contextMenu.with(ContextMenuEntry.builder()
            .icon(SpriteCommon.TRASH)
            .text(Component.translatable("labymod,ui,button.reset"))
            .disabled(() -> {
              boolean resettable = milieuElement.isResettable();
              if(!resettable) {
                for(Milieu childMilieu : milieuElement.values()) {
                  if(childMilieu.isResettable()) {
                    resettable = true;
                    break;
                  }
                }
              }

              return !resettable;
            }).clickHandler((contextMenuEntry, o) -> MilieuWidget.this.resetMilieu(milieuElement)).build()
        );

        ClientPermission permission = getPermission();
        if(
            permission != null &&
            !permission.isDefaultEnabled() &&
            !permission.isEnabled() && this.labyAPI.minecraft().isIngame() &&
            this.milieu.canForceEnable()
        ) {
          String forceEnableKey = "labymod.ui.settings.serverFeatures.forceEnable.%s";
          contextMenu.addEntry(
              ContextMenuEntry.builder()
                  .icon(SpriteCommon.SHIELD)
                  .text(Component.translatable(String.format(forceEnableKey, "entry")))
                  .clickHandler((entry) -> {
                    PopupWidget.builder()
                        .text(Component.translatable(String.format(forceEnableKey, "title")))
                        .text(Component.translatable(String.format(forceEnableKey, "warning")))
                        .build()
                        .displayInOverlay();

                    return true;
                  })
                  .build()
          );
        }
      }
    }
  }

  private void resetMilieu(MilieuElement milieu) {
    milieu.reset();
    callActionListeners();

    for(Milieu childMilieu : milieu.values()) {
      if(childMilieu.isElement()) resetMilieu(childMilieu.asElement());
    }
  }

  private void initializeInfoWidget(FlexibleContentWidget header) {
    ClientPermission permission = getPermission();

    if(permission != null) {
      addId("permission-required");

      boolean permissionDefaultEnabled = permission.isDefaultEnabled();
      boolean permissionEnabled = (!this.labyAPI.serverController().isConnected() || permission.isEnabled());

      if((!permissionDefaultEnabled && !this.labyAPI.minecraft().isIngame()) || !permissionEnabled) {
        String translation = permissionEnabled ? "labymod.ui.settings.serverFeatures.disabled" : "labymod.ui.settings.serverFeature.disabledDefault";
        TranslatableComponent translatableComponent = Component.translatable(translation, NamedTextColor.RED);

        IconWidget widget = new IconWidget(null);
        widget.addId("permission-warning");
        widget.setHoverComponent(translatableComponent);
        header.addContent(widget);

        if(!permissionEnabled) {
          DivWidget permissionDisabled = new DivWidget();
          permissionDisabled.addId("permission-disabled");
          permissionDisabled.setPressable(() -> {
            addContent(permissionDisabled);
            header.setHoverComponent(translatableComponent);
          });
        }
      }
    }

    Icon icon = this.milieu.getIcon();
    if(icon != null) {
      IconWidget widget = new IconWidget(icon);
      widget.addId("milieu-icon");
      header.addContent(widget);
    }

    ComponentWidget displayName = ComponentWidget.component(this.milieu.displayName());
    displayName.addId("display-name");
    header.addFlexibleContent(displayName);
  }

  private ClientPermission getPermission() {
    if(!this.milieu.isElement()) return null;

    String requiredPermission = this.milieu.asElement().getRequiredPermission();
    return (requiredPermission == null)
        ? null
        : this.labyAPI.permissionRegistry().getPermission(requiredPermission);
  }

  private void initializeInteractionWidget(FlexibleContentWidget header, Widget[] inputWidgets, final Widget indicator) {
    MilieuHandler handler = this.milieu.handler();
    SwitchWidget advancedSwitch = null;
    SwitchWidget indicatorSwitch = null;

    if(inputWidgets != null) {
      for(Widget inputWidget : inputWidgets) {
        if(inputWidget instanceof MilieuActivitySupplier) {
          inputWidget.setPressable(() -> {
            this.advancedCallback.accept((this.milieu));
          });
        }
      }

      if(this.milieu.hasAdvancedButton() || (inputWidgets != null && inputWidgets[0] instanceof MilieuActivityWidget) || (handler != null && handler.opensActivity(this.milieu))) {
        ButtonWidget advancedButton = null;

        if(advancedButton == null) advancedButton = ButtonWidget.advancedSettings();
        advancedButton.addId("advanced-button");
        advancedButton.setPressable(() -> {
          this.advancedCallback.accept(this.milieu);
        });

        header.addContent(advancedButton);

        if(this.milieu.hasControlButton() && this.milieu instanceof MilieuElement) {
          final MilieuElement milieuElement = (MilieuElement) this.milieu;
          SwitchWidget controlButton = SwitchWidget.create((value) -> {
            MilieuAccessor accessor = milieuElement.getAdvancedAccessor();
            accessor.set(value);
          });

          controlButton.setValue(milieuElement.getAdvancedAccessor().get());
          controlButton.addId("advanced-control-button");
          header.addContent(controlButton);

          advancedSwitch = controlButton;
        }
      }

      if(this.milieu instanceof ListMilieuEntry) {
        ButtonWidget advancedButton = ButtonWidget.deleteButton();
        advancedButton.addId("button-delete");
        advancedButton.setPressable(() -> {
          ((ListMilieuEntry) this.milieu).remove();
          this.advancedCallback.accept(null);
        });

        header.addContent(advancedButton);
      }

      if(inputWidgets != null && !this.milieu.asElement().isExtended()) {
        FlexibleContentWidget div = new FlexibleContentWidget();
        div.addId("input-wrapper");

        for(int i = inputWidgets.length - 1; i >= 0; i--) {
          Widget inputWidget = inputWidgets[i];

          if(indicatorSwitch == null && inputWidget instanceof SwitchWidget) {
            indicatorSwitch = (SwitchWidget) inputWidget;
          }

          if(!(inputWidget instanceof MilieuActivityWidget)) {
            inputWidget.reset();
            inputWidget.addId("input-widget");

            if(i == 0) div.addContent(inputWidget);
            else div.addFlexibleContent(inputWidget);
          }
        }

        if(!div.getChildren().isEmpty()) header.addContent(div);
      }

      final SwitchWidget switchWidget = (indicatorSwitch != null) ? indicatorSwitch : advancedSwitch;
      if(switchWidget != null) {
        indicator.addId(switchWidget.getValue() ? "status-enabled" : "status-disabled");
        switchWidget.setActionListener("indicator", () -> {
          indicator.replaceId(
              switchWidget.getValue() ? "status-disabled" : "status-enabled",
              switchWidget.getValue() ? "status-enabled" : "status-disabled"
          );
        });
      }
    }
  }

}
