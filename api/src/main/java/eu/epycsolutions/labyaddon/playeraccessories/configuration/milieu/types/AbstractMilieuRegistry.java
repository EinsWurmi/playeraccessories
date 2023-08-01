package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types;

import net.labymod.api.client.gui.icon.Icon;

public abstract class AbstractMilieuRegistry extends AbstractMilieu {

  private boolean holdable = true;

  protected AbstractMilieuRegistry(String id, Icon icon) { super(id, icon); }

  public AbstractMilieuRegistry holdable(boolean holdable) {
    this.holdable = holdable;
    return this;
  }

  public boolean holdable() { return this.holdable; }

}
