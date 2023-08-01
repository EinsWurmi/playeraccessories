package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.impl;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ConfigPropertyMilieuAccessor implements MilieuAccessor {

  private final MilieuElement milieu;
  private final ConfigProperty configProperty;
  private final Config config;
  private final Field field;

  public ConfigPropertyMilieuAccessor(MilieuElement milieu, ConfigProperty configProperty, Config config, Field field) {
    this.milieu = milieu;
    this.configProperty = configProperty;
    this.config = config;
    this.field = field;
  }

  @Override
  public Class<?> getType() {
    return this.configProperty.getType();
  }

  @Override
  public @Nullable Type getGenericType() {
    return this.configProperty.getGenericType();
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
    this.configProperty.set(value);
  }

  @Override
  public <T> T get() {
    return (T) configProperty.get();
  }

  @Override
  public <T> ConfigProperty<T> property() {
    return this.configProperty;
  }

  @Override
  public MilieuElement milieu() {
    return this.milieu;
  }

}
