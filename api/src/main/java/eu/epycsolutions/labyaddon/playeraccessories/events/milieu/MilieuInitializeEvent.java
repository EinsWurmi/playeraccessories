package eu.epycsolutions.labyaddon.playeraccessories.events.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;

@LabyEvent(background = true)
public record MilieuInitializeEvent(Milieu milieu) implements Event {

}
