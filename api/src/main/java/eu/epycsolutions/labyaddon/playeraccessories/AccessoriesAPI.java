package eu.epycsolutions.labyaddon.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.api.generated.ReferenceStorage;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.AccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.AbstractMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.environ.EnvironService;
import net.labymod.api.LabyAPI;
import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface AccessoriesAPI {

  LabyAPI labyAPI();

  EnvironService environService();

  ReferenceStorage referenceStorage();

  AccessoriesConfig accessoriesConfig();

  AbstractMilieuRegistry coreMilieuRegistry();

  WidgetRegistry widgetRegistry();

}
