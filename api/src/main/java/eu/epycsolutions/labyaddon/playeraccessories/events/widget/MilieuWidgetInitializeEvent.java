package eu.epycsolutions.labyaddon.playeraccessories.events.widget;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import net.labymod.api.client.gui.screen.ParentScreen;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;
import java.util.List;

@LabyEvent(background = true)
public record MilieuWidgetInitializeEvent(ParentScreen screen, Milieu holder, List<Widget> milieus) implements Event { }
