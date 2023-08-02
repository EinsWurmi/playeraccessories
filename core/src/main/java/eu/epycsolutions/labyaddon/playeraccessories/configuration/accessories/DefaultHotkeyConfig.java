package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.input.KeybindWidget.KeyBindMilieu;
import net.labymod.api.client.gui.screen.key.Key;

public class DefaultHotkeyConfig extends Config implements HotkeyConfig {

  @KeyBindMilieu
  private final ConfigProperty<Key> playerAccessoriesKey = new ConfigProperty<>(Key.F10);

  @Override
  public ConfigProperty<Key> playerAccessoriesKey() {
    return this.playerAccessoriesKey;
  }

}
