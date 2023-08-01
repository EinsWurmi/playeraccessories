package eu.epycsolutions.labyaddon.playeraccessories.configuration.expections;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;

public class ConfigLoadException extends ConfigurationException {

  public ConfigLoadException(Class<? extends ConfigAccessor> clazz, Throwable cause) {
    super("Could not load the Configuration \"" + clazz.getSimpleName() + "\"", cause);
  }

}
