package eu.epycsolutions.labyaddon.playeraccessories.events;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import net.labymod.api.event.Event;

public record MilieuCreateEvent(Milieu milieu) implements Event {

}
