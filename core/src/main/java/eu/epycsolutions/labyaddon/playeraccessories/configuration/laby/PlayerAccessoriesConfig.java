package eu.epycsolutions.labyaddon.playeraccessories.configuration.laby;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@ConfigName("player-accessories")
public class PlayerAccessoriesConfig extends AddonConfig {

  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true).addChangeListener((property, oldValue, newValue) -> {

  });

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

}
