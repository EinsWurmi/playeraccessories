package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.environ.LoadedEnviron;
import java.util.Map;

public interface AccessoriesConfig extends ConfigAccessor {

  IngameConfig ingame();

  AppearanceConfig appearance();

  HotkeyConfig hotkey();

  NotificationsConfig notifications();

  OtherConfig other();

  Map<String, LoadedEnviron> enabledEnvirons();

}
