package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget;

import eu.epycsolutions.labyaddon.playeraccessories.Accessories;
import eu.epycsolutions.labyaddon.playeraccessories.AccessoriesAPI;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.reference.annotation.Referenceable;
import java.lang.annotation.Annotation;

@Referenceable
public interface WidgetRegistry {

  void loadWidgetStorage(ClassLoader loader);

  void register(Class<? extends Widget> clazz);

  Widget[] createWidgets(Milieu milieu, Annotation annotation, MilieuInfo<?> info, MilieuAccessor accessor);

  default Widget[] createWidgets(MilieuInfo<?> info, MilieuAccessor accessor) {
    return this.createWidgets(null, info, accessor);
  }

  default Widget[] createWidgets(Milieu milieu, MilieuInfo<?> info, MilieuAccessor accessor) {
    for(Annotation annotation : info.member().getDeclaredAnnotations()) {
      AccessoriesAPI accessoriesAPI = Accessories.accessoriesAPI();
      WidgetRegistry widgetRegistry = accessoriesAPI.widgetRegistry();
      Widget[] widgets = widgetRegistry.createWidgets(milieu, annotation, info, accessor);

      if(widgets != null) {
        if(milieu.isElement()) {
          milieu.asElement().setAnnotation(annotation);
          milieu.asElement().setExtended(annotation.annotationType().getAnnotation(MilieuElement.class).extended());
        }

        return widgets;
      }
    }

    return null;
  }

  Class<? extends Widget> getWidgetTypeByAnnotation(Annotation annotation);

}
