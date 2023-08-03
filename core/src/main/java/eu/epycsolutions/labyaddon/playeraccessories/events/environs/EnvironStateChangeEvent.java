package eu.epycsolutions.labyaddon.playeraccessories.events.environs;

import net.labymod.api.event.Event;

public class EnvironStateChangeEvent implements Event {

  private final String namespace;
  private final State previousState;
  private final State newState;

  public EnvironStateChangeEvent(String namespace, Boolean previousState, Boolean newState) {
    this.namespace = namespace;
    this.previousState = State.of(previousState);
    this.newState = State.of(newState);
  }

  public String namespace() {
    return this.namespace;
  }

  public State previousState() {
    return this.previousState;
  }

  public State newState() {
    return this.newState;
  }

  public enum State {
    UNKNOWN,
    ENABLED,
    DISABLED;

    public static State of(Boolean value) {
      if(value == null) return UNKNOWN;
      return value ? ENABLED : DISABLED;
    }
  }

}
