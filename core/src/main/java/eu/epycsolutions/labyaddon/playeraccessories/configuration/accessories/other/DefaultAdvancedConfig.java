package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.other;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.SwapWidget.SwapMilieu;

public class DefaultAdvancedConfig extends Config implements AdvancedConfig {

  @SwapMilieu
  private final ConfigProperty<Boolean> devSettingsEnabled = new ConfigProperty<>(false);

  @SwapMilieu
  private final ConfigProperty<Boolean> enableAddonDebugging = new ConfigProperty<>(false);

  @SwapMilieu
  private final ConfigProperty<Boolean> enableEnvironDebugging = new ConfigProperty<>(false);

  @Override
  public ConfigProperty<Boolean> devSettingsEnabled() {
    return this.devSettingsEnabled;
  }

  @Override
  public ConfigProperty<Boolean> enableAddonDebugging() {
    return this.enableAddonDebugging;
  }

  @Override
  public ConfigProperty<Boolean> enableEnvironDebugging() {
    return this.enableEnvironDebugging;
  }

}
