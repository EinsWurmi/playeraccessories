package eu.epycsolutions.labyaddon.playeraccessories.events;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import net.labymod.api.event.Event;
import net.labymod.api.event.Phase;

public record MilieuResetEvent(Phase phase, MilieuElement milieu) implements Event {

}
