package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigLoadException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigSaveException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.ConfigName;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.ConfigPath;
import net.labymod.api.util.reflection.Reflection;
import java.io.IOException;

public interface ConfigLoader {

  <T extends ConfigAccessor> T load(Class<T> clazz) throws ConfigLoadException;

  void save(ConfigAccessor config) throws ConfigSaveException;

  Object serialize(ConfigAccessor config) throws Exception;

  static String getName(Class<? extends ConfigAccessor> clazz) {
    ConfigName annotation = null;

    for(Class<?> treeClass : Reflection.getClassTree(clazz)) {
      ConfigName treeClassAnnotation = treeClass.getAnnotation(ConfigName.class);

      if(treeClassAnnotation != null) {
        annotation = treeClassAnnotation;
        break;
      }
    }

    return annotation == null ? "settings" : annotation.value();
  }

  default String getRelativePath(Class<? extends ConfigAccessor> clazz) {
    ConfigPath annotation = null;

    for(Class<?> treeClass : Reflection.getClassTree(clazz)) {
      ConfigPath treeClassAnnotation = treeClass.getAnnotation(ConfigPath.class);

      if(treeClassAnnotation != null) {
        annotation = treeClassAnnotation;
        break;
      }
    }

    return String.format("%s/%s", annotation == null ? "." : annotation.value(), ConfigLoader.getName(clazz));
  }

  void setVariable(String key, Object value);

  String getFileExtension();

  void invalidate(Class<? extends ConfigAccessor> type) throws IOException;

}
