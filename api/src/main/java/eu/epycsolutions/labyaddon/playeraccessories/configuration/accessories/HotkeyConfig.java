package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import net.labymod.api.client.gui.screen.key.Key;

public interface HotkeyConfig extends ConfigAccessor {

  ConfigProperty<Key> playerAccessoriesKey();

}
