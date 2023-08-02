package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.appearance.DefaultNavigationConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.appearance.NavigationConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;

public class DefaultAppearanceConfig extends Config implements AppearanceConfig {

  private final DefaultNavigationConfig navigation = new DefaultNavigationConfig();

  @Override
  public NavigationConfig navigation() {
    return this.navigation;
  }

}
