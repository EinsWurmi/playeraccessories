package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.swappable.BooleanSwappableHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.swappable.StringSwappableHandler;
import net.labymod.api.models.Implements;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Implements(SwappableHandlerRegistry.class)
public class DefaultSwappableHandlerRegistry implements SwappableHandlerRegistry {

  private final Map<Class<? extends SwappableHandler>, SwappableHandler> handlerMap = new HashMap<>();

  public DefaultSwappableHandlerRegistry() {
    registerHandler(new BooleanSwappableHandler());
    registerHandler(new StringSwappableHandler());
  }

  @Override
  public void registerHandler(SwappableHandler handler) {
    this.handlerMap.put(handler.getClass(), handler);
  }

  @Override
  public SwappableHandler getHandler(Class<? extends SwappableHandler> clazz) {
    return this.handlerMap.get(clazz);
  }

}
