package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import java.util.ArrayList;

public interface AccessoriesConfig extends ConfigAccessor {

  IngameConfig ingame();

  AppearanceConfig appearance();

  HotkeyConfig hotkey();

  NotificationsConfig notifications();

  OtherConfig other();

  ArrayList<String> enabledEnvirons();

}
