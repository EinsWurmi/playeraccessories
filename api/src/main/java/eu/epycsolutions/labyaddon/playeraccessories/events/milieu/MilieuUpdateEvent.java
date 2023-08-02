package eu.epycsolutions.labyaddon.playeraccessories.events.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;
import net.labymod.api.event.Phase;

@LabyEvent(background = true)
public class MilieuUpdateEvent implements Event {

  private final Phase phase;
  private final MilieuElement milieu;
  private Object value;

  public MilieuUpdateEvent(Phase phase, MilieuElement milieu, Object value) {
    this.phase = phase;
    this.milieu = milieu;
    this.value = value;
  }

  public Phase phase() {
    return phase;
  }

  public MilieuElement milieu() {
    return milieu;
  }

  public <T> T getValue() {
    return (T) value;
  }

  public <T> void setValue(T value) {
    this.value = value;
  }

}
