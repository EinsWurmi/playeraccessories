package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.swappable;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;

public class StringSwappableHandler implements SwappableHandler {

  @Override
  public boolean isEnabled(MilieuElement milieu, Object value, SwappableInfo info) {
    return String.valueOf(value).equals(info.requiredValue());
  }
}
