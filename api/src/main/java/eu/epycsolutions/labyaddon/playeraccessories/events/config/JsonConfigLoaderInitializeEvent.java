package eu.epycsolutions.labyaddon.playeraccessories.events.config;

import com.google.gson.GsonBuilder;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;

@LabyEvent(background = true)
public record JsonConfigLoaderInitializeEvent(GsonBuilder gsonBuilder) implements Event {

}
