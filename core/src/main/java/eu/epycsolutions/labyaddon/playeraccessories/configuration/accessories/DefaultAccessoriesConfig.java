package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.ConfigName;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.Exclude;
import java.util.ArrayList;

@ConfigName("settings")
public class DefaultAccessoriesConfig extends Config implements AccessoriesConfig {

  private final DefaultIngameConfig ingame = new DefaultIngameConfig();

  private final DefaultAppearanceConfig appearance = new DefaultAppearanceConfig();

  private final DefaultNotificationsConfig notifications = new DefaultNotificationsConfig();

  private final DefaultHotkeyConfig hotkeys = new DefaultHotkeyConfig();

  private final DefaultOtherConfig other = new DefaultOtherConfig();

  @Exclude
  private final ArrayList<String> enabledEnvirons = new ArrayList<>();

  @Override
  public IngameConfig ingame() {
    return this.ingame;
  }

  @Override
  public AppearanceConfig appearance() {
    return this.appearance;
  }

  @Override
  public NotificationsConfig notifications() {
    return this.notifications;
  }

  @Override
  public HotkeyConfig hotkey() {
    return this.hotkeys;
  }

  @Override
  public OtherConfig other() {
    return this.other;
  }

  public ArrayList<String> enabledEnvirons() {
    return this.enabledEnvirons;
  }

}
