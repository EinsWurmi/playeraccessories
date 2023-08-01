package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;

public class SwappableInfo {

  private final String swapId;
  private final boolean invert;
  private final String requiredValue;

  private MilieuAccessor swapAccessor;
  private SwappableHandler handler;

  public SwappableInfo(String swapId, boolean invert, String requiredValue) {
    this.swapId = swapId;
    this.invert = invert;
    this.requiredValue = requiredValue;
  }

  public String swapId() {
    return this.swapId;
  }

  public boolean isInvert() {
    return this.invert;
  }

  public String requiredValue() {
    return this.requiredValue;
  }

  public void setHandler(SwappableHandler handler) {
    this.handler = handler;
  }

  public SwappableHandler handler() {
    return this.handler;
  }

  public void setSwapAccessor(MilieuAccessor accessor) {
    this.swapAccessor = accessor;
  }

  public MilieuAccessor accessor() {
    return this.swapAccessor;
  }

}
