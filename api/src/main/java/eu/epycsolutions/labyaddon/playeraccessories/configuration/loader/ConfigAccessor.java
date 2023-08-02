package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.RootMilieuRegistry;
import java.util.List;

public interface ConfigAccessor {

  List<Milieu> toMilieus();

  List<Milieu> toMilieus(Milieu milieu);

  RootMilieuRegistry asRegistry(String id);

}
