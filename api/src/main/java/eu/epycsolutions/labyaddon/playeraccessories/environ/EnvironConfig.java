package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;

public abstract class EnvironConfig extends Config {

  public abstract ConfigProperty<Boolean> enabled();

}
