package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.ConfigName;

@ConfigName("settings")
public class DefaultAccessoriesConfig extends Config implements AccessoriesConfig {

  private final DefaultAppearanceConfig appearance = new DefaultAppearanceConfig();

  private final DefaultDeveloperConfig developer = new DefaultDeveloperConfig();

  private final DefaultHotkeyConfig hotkeys = new DefaultHotkeyConfig();

  @Override
  public AppearanceConfig appearanceConfig() {
    return this.appearance;
  }

  @Override
  public DeveloperConfig developerConfig() {
    return this.developer;
  }

  @Override
  public HotkeyConfig hotkeyConfig() {
    return this.hotkeys;
  }

}
