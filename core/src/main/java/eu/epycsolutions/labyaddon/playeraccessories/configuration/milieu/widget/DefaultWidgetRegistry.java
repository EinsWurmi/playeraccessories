package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.ServiceLoadEvent;
import net.labymod.api.models.Implements;
import net.labymod.api.service.CustomServiceLoader;
import net.labymod.api.service.CustomServiceLoader.ServiceType;
import net.labymod.api.util.logging.Logging;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Implements(WidgetRegistry.class)
public class DefaultWidgetRegistry implements WidgetRegistry {

  private final Logging logger;

  private final Map<Class<? extends Annotation>, Class<? extends Widget>> registry = new HashMap<>();

  @Inject
  public DefaultWidgetRegistry() {
    Laby.labyAPI().eventBus().registerListener(this);
    this.logger = Logging.create(WidgetRegistry.class);
  }

  @Subscribe
  public void loadWidgetStorages(ServiceLoadEvent event) {
    loadWidgetStorage(event.classLoader());
  }

  @Override
  public void loadWidgetStorage(ClassLoader loader) {
    List<Class<? extends Widget>> storage = new ArrayList<>();
    CustomServiceLoader<WidgetStorage> storageLoader = CustomServiceLoader.load(WidgetStorage.class, loader, ServiceType.ADVANCED);

    for(WidgetStorage widgetStorage : storageLoader) {
      widgetStorage.store(storage);
    }

    storage.forEach(this::register);
    storage.clear();
  }

  @Override
  public void register(Class<? extends Widget> clazz) {
    Class[] arrayOfClass;
    int i;
    byte b;

    for(arrayOfClass = clazz.getClasses(), i = arrayOfClass.length, b = 0; b < i;) {
      Class<?> subClass = arrayOfClass[b];
      if(subClass.getAnnotation(MilieuElement.class) == null) {
        b++;
        continue;
      }

      this.registry.put((Class<? extends Annotation>) subClass, clazz);
      return;
    }
  }

  @Override
  public Widget[] createWidgets(Milieu milieu, Annotation annotation, MilieuInfo<?> info, MilieuAccessor accessor) {
    try {
      Class<? extends Widget> clazz = getWidgetTypeByAnnotation(annotation);
      if(clazz == null) return null;

      for(Class<?> declaredClass : clazz.getDeclaredClasses()) {
        if(WidgetFactory.class.isAssignableFrom(declaredClass)) {
          WidgetFactory<Annotation, Widget> factory = (WidgetFactory<Annotation, Widget>) declaredClass.getConstructor(new Class[0]).newInstance(new Object[0]);
          return createWidgets(factory, milieu, annotation, info, accessor);
        }
      }
    } catch(Exception exception) {
      exception.printStackTrace();
    }

    return null;
  }

  @Override
  public Class<? extends Widget> getWidgetTypeByAnnotation(Annotation annotation) {
    return this.registry.get(annotation.annotationType());
  }

  public Widget[] createWidgets(WidgetFactory<Annotation, Widget> widgetFactory, Milieu milieu, Annotation annotation, MilieuInfo<?> info, MilieuAccessor accessor) {
    Class<?>[] types = widgetFactory.types();
    if(types.length != 0) {
      String[] typeString = new String[types.length];
      boolean typesMatch = false;

      for(int i = 0; i < types.length; i++) {
        Class<?> type = types[i];
        if(accessor.getType() == type || type.isAssignableFrom(accessor.getType())) typesMatch = true;
        typeString[i] = type.getSimpleName();
      }

      if(!typesMatch) {
        this.logger.error("The milieu field \"{}\" has the type {} but has to be {}",
            milieu.getTranslationKey(),
            accessor.getType().getSimpleName(),
            (typeString.length == 1)
              ? (": " + typeString[0])
              : (" one of these types: " + String.join(", ", typeString)));

        return null;
      }
    }

    return widgetFactory.create(milieu, annotation, info, accessor);
  }

}
