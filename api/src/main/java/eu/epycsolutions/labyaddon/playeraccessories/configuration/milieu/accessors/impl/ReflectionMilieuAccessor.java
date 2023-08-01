package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.impl;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.events.MilieuUpdateEvent;
import net.labymod.api.Laby;
import net.labymod.api.event.Phase;
import net.labymod.api.util.reflection.Reflection;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ReflectionMilieuAccessor implements MilieuAccessor {

  private final MilieuElement milieu;
  private final Field field;
  private final Config config;

  private final ConfigProperty<?> property;

  public ReflectionMilieuAccessor(MilieuElement milieu, Field field, Config config) {
    this.milieu = milieu;
    this.field = field;
    this.config = config;

    field.setAccessible(true);

    this.property = new ConfigProperty<>(get());
  }

  @Override
  public Class<?> getType() {
    return this.field.getType();
  }

  @Override
  public @Nullable Type getGenericType() {
    return this.field.getGenericType();
  }

  @Override
  public Field getField() {
    return this.field;
  }

  @Override
  public Config config() {
    return this.config;
  }

  @Override
  public <T> void set(T value) {
    if(this.milieu.isInitialized()) {
      MilieuUpdateEvent event = new MilieuUpdateEvent(Phase.PRE, this.milieu, value);
      Laby.labyAPI().eventBus().fire(event);

      value = event.getValue();
    }

    Reflection.invokeSetterField(config, field, value);

    MilieuUpdateEvent event = new MilieuUpdateEvent(Phase.POST, this.milieu, value);
    Laby.labyAPI().eventBus().fire(event);
  }

  @Override
  public <T> T get() {
    return Reflection.invokeGetterField(config, field);
  }

  @Override
  public <T> ConfigProperty<T> property() {
    this.property.set(get());
    return (ConfigProperty<T>) property;
  }

  @Override
  public MilieuElement milieu() {
    return this.milieu;
  }

}
