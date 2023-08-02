package eu.epycsolutions.labyaddon.playeraccessories.configuration;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.AccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.DefaultAccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigProvider;

public class AccessoriesConfigProvider extends ConfigProvider<AccessoriesConfig> {

  public static final AccessoriesConfigProvider INSTANCE = new AccessoriesConfigProvider();

  @Override
  protected Class<? extends ConfigAccessor> getType() {
    return DefaultAccessoriesConfig.class;
  }
}
