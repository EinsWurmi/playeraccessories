package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface MilieuAccessor {

  Class<?> getType();

  @Nullable
  Type getGenericType();

  Field getField();

  Config config();

  <T> void set(T value);

  <T> T get();

  <T> ConfigProperty<T> property();

  default boolean isSet() {
    return (get() != null);
  }

  MilieuElement milieu();

}
