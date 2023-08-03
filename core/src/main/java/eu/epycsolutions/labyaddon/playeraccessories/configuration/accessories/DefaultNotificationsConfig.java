package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.SwapWidget.SwapMilieu;

public class DefaultNotificationsConfig extends Config implements NotificationsConfig {

  @SwapMilieu
  private final ConfigProperty<Boolean> enableAddonNotifications = new ConfigProperty<>(true);

  @Override
  public ConfigProperty<Boolean> enableAddonNotifications() {
    return this.enableAddonNotifications;
  }

}
