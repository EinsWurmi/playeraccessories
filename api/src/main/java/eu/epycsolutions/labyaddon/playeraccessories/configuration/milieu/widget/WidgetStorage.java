package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget;

import net.labymod.api.client.gui.screen.widget.Widget;
import java.util.List;

@FunctionalInterface
public interface WidgetStorage {

  void store(List<Class<? extends Widget>> widgets);

}
