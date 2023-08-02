package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.appearance;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;

public interface NavigationConfig extends ConfigAccessor {

  ConfigProperty<Boolean> showEnvironList();

}
