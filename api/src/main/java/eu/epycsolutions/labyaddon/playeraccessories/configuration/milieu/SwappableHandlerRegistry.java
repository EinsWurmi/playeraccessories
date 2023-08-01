package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface SwappableHandlerRegistry {

  void registerHandler(SwappableHandler handler);

  SwappableHandler getHandler(Class<? extends SwappableHandler> clazz);

}
