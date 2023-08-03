package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.other.AdvancedConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.other.DefaultAdvancedConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;

public class DefaultOtherConfig extends Config implements OtherConfig {

  private final DefaultAdvancedConfig advanced = new DefaultAdvancedConfig();

  @Override
  public AdvancedConfig advanced() {
    return this.advanced;
  }

}
