package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import net.labymod.api.client.gui.screen.activity.Activity;

public interface MilieuHandler {

  void created(Milieu milieu);

  void initialized(Milieu milieu);

  default void reset(Milieu milieu) { }

  default boolean isEnabled(Milieu milieu) { return true; }

  default boolean opensActivity(Milieu milieu) { return false; }

  default Activity activity(Milieu milieu) { return null; }

}
