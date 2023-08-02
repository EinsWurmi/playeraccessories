package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.appearance;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.SwapWidget.SwapMilieu;

public class DefaultNavigationConfig extends Config implements NavigationConfig {

  @SwapMilieu
  private final ConfigProperty<Boolean> showEnvironList = new ConfigProperty<>(false);

  @Override
  public ConfigProperty<Boolean> showEnvironList() {
    return this.showEnvironList;
  }
}
