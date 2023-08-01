package eu.epycsolutions.labyaddon.playeraccessories.configuration.expections;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;

public class ConfigSaveException extends ConfigurationException {

  public ConfigSaveException(ConfigAccessor configAccessor, Throwable cause) {
    super("Could not save the Configuration \"" + configAccessor.getClass().getSimpleName() + "\"", cause);
  }

}