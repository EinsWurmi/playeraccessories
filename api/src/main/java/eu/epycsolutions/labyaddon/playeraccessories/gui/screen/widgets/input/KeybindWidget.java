package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widgets.input;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuFactory;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuWidget;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetFactory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.widget.action.Selectable;
import net.labymod.api.util.I18n;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@AutoWidget
@MilieuWidget
public class KeybindWidget extends TextFieldWidget {

  private final Selectable<Key> selectable;

  private Key key = null;

  private boolean listening = false;
  private boolean acceptMouseButtons = true;

  private String lastVisibleText;

  public KeybindWidget(Selectable<Key> selectable) {
    this.selectable = selectable;
    this.placeholder = Component.translatable("labymod.ui.keybind.pressKey");
  }

  @Override
  public void tick() {
    if(this.listening && !isFocused()) this.listening = false;
  }

  @Override
  public boolean mouseClicked(MutableMouse mouse, MouseButton button) {
    if(isListening() && this.acceptMouseButtons && isHovered()) {
      updateKey((Key) button);
      return true;
    }

    if(button == MouseButton.LEFT) this.listening = isHovered();

    return super.mouseClicked(mouse, button);
  }

  @Override
  public boolean keyPressed(Key key, InputType type) {
    if(isListening() && this.key == Key.ESCAPE) return updateKey(Key.NONE);
    if(isListening() && !key.isUnknown()) return updateKey(this.key);

    return false;
  }

  private boolean updateKey(Key key) {
    this.key = key;
    this.selectable.select(this.key);
    setFocused(false);
    this.listening = false;

    return true;
  }

  @Override
  public boolean shouldHandleEscape() {
    return isListening();
  }

  @Override
  public boolean isCursorVisible() {
    return false;
  }

  public KeybindWidget acceptMouseButtons(boolean acceptMouseButtons) {
    this.acceptMouseButtons = acceptMouseButtons;
    return this;
  }

  public KeybindWidget key(Key key) {
    this.key = key;
    this.selectable.select(key);
    return this;
  }

  public Key key() {
    return this.key;
  }

  @Override
  public String getVisibleText() {
    String visibleText = super.getVisibleText();

    if(this.lastVisibleText == null) this.lastVisibleText = getFormattedText();
    if(!this.lastVisibleText.equals(visibleText)) {
      String formattedText = getFormattedText();

      if(visibleText.equals(formattedText)) setHoverComponent(null);
      else setHoverComponent(Component.text(formattedText));
    }

    return visibleText;
  }

  @Override
  protected String getFormattedText() {
    if(this.key == null || this.key == Key.NONE) return I18n.translate("labymod.ui.keybind.none");
    if(this.key instanceof MouseButton) return I18n.translate("labymod.ui.keybind.mouse", this.key.getName());

    return I18n.translate("labymod.ui.keybind.keyboard", this.key.getName());
  }

  @Override
  public boolean shouldDisplayPlaceHolder() {
    return isListening();
  }

  public boolean isListening() {
    return this.listening;
  }


  @MilieuFactory
  public static class Factory implements WidgetFactory<KeyBindMilieu, KeybindWidget> {

    @Override
    public KeybindWidget[] create(Milieu milieu, KeyBindMilieu annotation, MilieuAccessor accessor) {
      Objects.requireNonNull(accessor);
      KeybindWidget widget = new KeybindWidget(accessor::set);
      widget.acceptMouseButtons(annotation.acceptMouseButtons());
      widget.key(accessor.get());
      widget.placeholderTranslatable = milieu.getTranslationKey() + ".placeholder";
      accessor.property().addChangeListener((t, oldValue, newValue) -> widget.updateKey((newValue instanceof Key) ? (Key) newValue : Key.NONE));

      return new KeybindWidget[] { widget };
    }

    public Class<?>[] types() {
      return new Class[] { Key.class };
    }

  }

  @MilieuElement
  @Target({ ElementType.FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface KeyBindMilieu {
    boolean acceptMouseButtons() default false;
  }

}
