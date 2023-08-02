package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.SwapWidget.SwapMilieu;

public class DefaultDeveloperConfig extends Config implements DeveloperConfig {

  @SwapMilieu
  ConfigProperty<Boolean> experimental = new ConfigProperty<>(false);

  @SwapMilieu
  ConfigProperty<Boolean> developer = new ConfigProperty<>(false);

  @Override
  public ConfigProperty<Boolean> experimentalSettings() {
    return this.experimental;
  }

  @Override
  public ConfigProperty<Boolean> developerSettings() {
    return this.developer;
  }

}
