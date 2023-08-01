package eu.epycsolutions.labyaddon.playeraccessories.events;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;
import net.labymod.api.event.Phase;

@LabyEvent(background = true)
public record MilieuUpdateEvent(Phase phase, MilieuElement milieu, Object value) implements Event {

}
