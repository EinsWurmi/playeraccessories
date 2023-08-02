package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;

public interface DeveloperConfig extends ConfigAccessor {

  ConfigProperty<Boolean> experimentalSettings();

  ConfigProperty<Boolean> developerSettings();

}
