package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.events.environs.EnvironStateChangeEvent;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.EnvironMeta;
import net.labymod.api.Laby;
import net.labymod.api.models.Implements;
import net.labymod.api.service.Service;
import org.jetbrains.annotations.NotNull;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Implements(EnvironService.class)
public class DefaultEnvironService extends Service implements EnvironService {

  private static final DefaultEnvironService instance = new DefaultEnvironService();

  private final Map<String, Environ> loadedEnvirons = new ConcurrentHashMap<>();
  private final Map<String, EnvironConfig> environConfigs = new HashMap<>();

  public static DefaultEnvironService instance() {
    return instance;
  }

  @Override
  public void registerEnviron(Environ environ) {
    this.loadedEnvirons.put(environ.info().getNamespace(), environ);
  }

  @Override
  public @NotNull Optional<Environ> getEnviron(String namespace) {
    return Optional.ofNullable(this.loadedEnvirons.get(namespace));
  }

  public Collection<Environ> getLoadedEnvirons() {
    return this.loadedEnvirons.values();
  }

  public Collection<Environ> getVisibleEnvirons() {
    List<Environ> environList = new ArrayList<>();

    for(Environ environ : getLoadedEnvirons()) {
      if(!environ.info().hasMeta(EnvironMeta.HIDDEN)) environList.add(environ);
    }

    return environList;
  }

  public void unloadEnviron(Environ environ) {
    this.loadedEnvirons.remove(environ.info().getNamespace());
  }

  @Override
  public boolean isEnabled(String namespace) {
    if(namespace.equals("player-accessories")) return true;

    Optional<Environ> environ = getEnviron(namespace);
    if(environ.isEmpty()) return true;

    EnvironConfig config = getConfiguration(namespace);
    return (config == null || (config.enabled() != null && config.enabled().get()));
  }

  @Override
  public void registerConfiguration(String namespace, EnvironConfig environConfig) {
    ConfigProperty<Boolean> enabled = environConfig.enabled();

    if(enabled != null) {
      Laby.fireEvent(new EnvironStateChangeEvent(namespace, null, enabled.get()));
      enabled.addChangeListener((type, oldValue, newValue) -> Laby.fireEvent(new EnvironStateChangeEvent(namespace, oldValue, newValue)));
    }

    this.environConfigs.put(namespace, environConfig);
  }

  @Override
  public boolean hasConfiguration(String namespace) {
    return environConfigs.containsKey(namespace);
  }

  public EnvironConfig getConfiguration(String namespace) {
    return this.environConfigs.get(namespace);
  }

}
