package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuFactory;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuWidget;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.swappable.StringSwappableHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetFactory;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@AutoWidget
@MilieuWidget
public class TextFieldWidget extends net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget {

  @MilieuFactory
  public static class Factory implements WidgetFactory<TextFieldMilieu, TextFieldWidget> {

    @Override
    public TextFieldWidget[] create(Milieu milieu, TextFieldMilieu annotation, MilieuAccessor accessor) {
      TextFieldWidget widget = new TextFieldWidget();
      widget.setText(accessor.get());
      Objects.requireNonNull(accessor);

      widget.updateListener(accessor::set);
      widget.placeholderTranslatable = milieu.getTranslationKey() + ".placeholder";

      if(annotation.maxLength() >= 0) widget.maximalLength(annotation.maxLength());
      accessor.property().addChangeListener((t, oldValue, newValue) -> widget.setText(String.valueOf(newValue)));

      return new TextFieldWidget[] { widget };
    }

    public Class<?>[] types() {
      return new Class[] { String.class };
    }

  }

  @MilieuElement(swappable = StringSwappableHandler.class)
  @Target({ ElementType.FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface TextFieldMilieu {
    int maxLength() default -1;
  }

}
