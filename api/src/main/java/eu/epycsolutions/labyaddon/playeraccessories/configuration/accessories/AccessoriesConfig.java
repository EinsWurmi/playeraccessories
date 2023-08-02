package eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;

public interface AccessoriesConfig extends ConfigAccessor {

  AppearanceConfig appearanceConfig();

  DeveloperConfig developerConfig();

  HotkeyConfig hotkeyConfig();

}
