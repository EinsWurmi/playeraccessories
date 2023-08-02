package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.appearance.NavigationConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;

public interface AppearanceConfig extends ConfigAccessor {

  NavigationConfig navigation();

}
