package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;

public interface SwappableHandler {

  boolean isEnabled(MilieuElement milieu, Object value, SwappableInfo info);

}
