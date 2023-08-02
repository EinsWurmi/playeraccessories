package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuFactory;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuWidget;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.swappable.BooleanSwappableHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetFactory;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.action.Swappable;
import net.labymod.api.Laby;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.key.mapper.KeyMapper;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.BoundsType;
import net.labymod.api.client.sound.SoundType;
import net.labymod.api.util.I18n;
import net.labymod.api.util.PrimitiveHelper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@AutoWidget
@MilieuWidget
public class SwapWidget extends SimpleWidget {

  private static final String DEFAULT_ENABLED = "player-accessories.ui.switch.enabled";
  private static final String DEFAULT_DISABLED = "player-accessories.ui.switch.disabled";

  private final Swappable swappable;
  private boolean value;

  private String enabledText = "";
  private String disabledText = "";

  private String enabledTranslatableKey = null;
  private String disabledTranslatableKey = null;

  private final LssProperty<Integer> textHoverColor = new LssProperty<>(NamedTextColor.GOLD.getValue());

  protected SwapWidget(Swappable swappable) {
    this.swappable = swappable;
  }

  public static SwapWidget create(Swappable swappable) {
    return translatable(DEFAULT_ENABLED, DEFAULT_DISABLED, swappable);
  }

  @Override
  public String getDefaultRendererName() {
    return "Swap";
  }

  public boolean getValue() {
    return this.value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  @Override
  public boolean onPress() {
    this.value = !this.value;
    if(this.swappable != null) this.swappable.swapValue(this.value);
    Laby.references().soundService().play(this.value ? SoundType.SWITCH_TOGGLE_ON : SoundType.SWITCH_TOGGLE_OFF);

    return true;
  }

  @Override
  public void tick() { }

  @Override
  public boolean mouseReleased(MutableMouse mouse, MouseButton button) {
    return true;
  }

  @Override
  public boolean mouseClicked(MutableMouse mouse, MouseButton mouseButton) {
    if(isHovered() && mouseButton == MouseButton.LEFT) {
      onPress();
      callActionListeners();
      return true;
    }

    return false;
  }

  @Override
  public boolean mouseScrolled(MutableMouse mouse, double scrollDelta) {
    return true;
  }

  public String getText() {
    return this.value ? this.enabledText : this.disabledText;
  }

  @Override
  public float getContentWidth(BoundsType type) {
    return 50.0F;
  }

  @Override
  public float getContentHeight(BoundsType type) {
    return 20.0F;
  }

  @Override
  public boolean isHoverComponentRendered() {
    return hasHoverComponent() ? super.isHoverComponentRendered() : isHovered();
  }

  public static SwapWidget translatable(String enabledTranslatableKey, String disabledTranslatableKey, Swappable swappable) {
    SwapWidget widget = new SwapWidget(swappable);
    widget.enabledTranslatableKey = enabledTranslatableKey;
    widget.disabledTranslatableKey = disabledTranslatableKey;

    return widget;
  }

  public static SwapWidget text(String enabledText, String disabledText, Swappable swappable) {
    SwapWidget widget = new SwapWidget(swappable);
    widget.enabledText = enabledText;
    widget.disabledText = disabledText;

    return widget;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    if(this.enabledTranslatableKey != null) {
      String enabledTranslation = I18n.getTranslation(this.enabledTranslatableKey);
      if(enabledTranslation == null) this.enabledText = I18n.translate(DEFAULT_ENABLED);
      else this.enabledText = enabledTranslation;
    }

    if(this.disabledTranslatableKey != null) {
      String disabledTranslation = I18n.getTranslation(this.disabledTranslatableKey);
      if(disabledTranslation == null) this.disabledText = I18n.translate(DEFAULT_DISABLED);
      else this.disabledText = disabledTranslation;
    }
  }

  public LssProperty<Integer> textHoverColor() {
    return this.textHoverColor;
  }


  @MilieuFactory
  public static class Factory implements WidgetFactory<SwapMilieu, Widget> {

    @Override
    public Widget[] create(Milieu milieu, SwapMilieu annotation, MilieuAccessor accessor) {
      if(annotation.hotkey()) {
        String metaKey = milieu.getId() + ".hotkey";
        SwapWidget swapWidget = createSwap(milieu, accessor);
        KeybindWidget keybindWidget = new KeybindWidget((key) -> accessor.config().configMeta().put(metaKey, key.getActualName()));

        if(accessor.config().hasConfigMeta(metaKey)) {
          Key key = KeyMapper.getKey(accessor.config().configMeta().get(metaKey));
          if(key != null) keybindWidget.key(key);
        }

        return new Widget[] { swapWidget, keybindWidget };
      }

      SwapWidget swapWidget = createSwap(milieu, accessor);
      return new SwapWidget[] { swapWidget };
    }

    public Class<?>[] types() {
      return PrimitiveHelper.BOOLEAN;
    }

    private SwapWidget createSwap(Milieu milieu, MilieuAccessor accessor) {
      Objects.requireNonNull(accessor);

      SwapWidget widget = new SwapWidget(accessor::set);
      widget.enabledTranslatableKey = milieu.getTranslationKey() + ".enabled";
      widget.disabledTranslatableKey = milieu.getTranslationKey() + ".disabled";
      widget.setValue(accessor.get());

      accessor.property().addChangeListener((t, oldValue, newValue) -> {
        widget.setValue((newValue instanceof Boolean && ((Boolean) newValue)));
      });

      return widget;
    }

  }

  @MilieuElement(swappable = BooleanSwappableHandler.class)
  @Target({ ElementType.FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface SwapMilieu {
    boolean hotkey() default false;
  }

}
