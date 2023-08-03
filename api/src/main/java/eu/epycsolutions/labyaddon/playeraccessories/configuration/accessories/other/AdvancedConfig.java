package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.other;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;

public interface AdvancedConfig extends ConfigAccessor {

  ConfigProperty<Boolean> devSettingsEnabled();

  ConfigProperty<Boolean> enableAddonDebugging();

  ConfigProperty<Boolean> enableEnvironDebugging();

}
