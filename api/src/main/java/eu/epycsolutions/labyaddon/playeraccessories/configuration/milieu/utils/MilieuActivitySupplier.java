package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.utils;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import net.labymod.api.client.gui.screen.activity.Activity;

public interface MilieuActivitySupplier {

  Activity activity(Milieu milieu);

}
