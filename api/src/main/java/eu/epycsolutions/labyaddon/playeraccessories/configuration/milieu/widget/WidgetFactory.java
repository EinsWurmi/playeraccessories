package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import net.labymod.api.client.gui.screen.widget.Widget;
import java.lang.annotation.Annotation;

public interface WidgetFactory<T extends Annotation, K extends Widget> {

  Class<?>[] types();

  default K[] create(Milieu milieu, T annotation, MilieuInfo<?> info, MilieuAccessor accessor) {
    return create(milieu, annotation, accessor);
  }

  default K[] create(Milieu milieu, T annotation, MilieuAccessor accessor) {
    throw new UnsupportedOperationException("Not implemented by " + getClass().getSimpleName());
  }

}
