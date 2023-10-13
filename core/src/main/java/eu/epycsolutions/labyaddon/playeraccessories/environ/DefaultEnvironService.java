package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.events.environ.lifecycle.EnvironEnableEvent;
import eu.epycsolutions.labyaddon.playeraccessories.events.environs.EnvironStateChangeEvent;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.EnvironMeta;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.Laby;
import net.labymod.api.models.Implements;
import net.labymod.api.service.Service;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

  private final Map<String, LoadedEnviron> loadedEnvirons = new ConcurrentHashMap<>();
  private final Map<String, LoadedEnviron> enabledEnvirons = new HashMap<>();
  private final Map<String, EnvironConfig> environConfigs = new HashMap<>();

  private boolean enabled;

  public static DefaultEnvironService getInstance() {
    return instance;
  }

  @Override
  public @NotNull Optional<LoadedEnviron> getEnviron(String namespace) {
    return Optional.ofNullable(this.loadedEnvirons.get(namespace));
  }

  @Override
  public Collection<LoadedEnviron> getEnabledEnvirons() {
    return this.enabledEnvirons.values();
  }

  @Override
  public Collection<LoadedEnviron> getLoadedEnvirons() {
    return this.loadedEnvirons.values();
  }

  @Override
  public Collection<LoadedEnviron> getVisibleEnvirons() {
    List<LoadedEnviron> list = new ArrayList<>();

    for(LoadedEnviron environ : getLoadedEnvirons()) {
      if(!environ.info().hasMeta(EnvironMeta.HIDDEN)) list.add(environ);
    }

    return list;
  }

  @Override
  public @Nullable InstalledEnvironInfo getEnvironInfo(String id) {
    if(this.loadedEnvirons.containsKey(id)) return this.loadedEnvirons.get(id).info();
    return null;
  }

  public void unloadEnviron(LoadedEnviron environ) {
    this.loadedEnvirons.remove(environ.info().getNamespace());
  }

  public void unloadEnvirons(Collection<LoadedEnviron> environCollection) {
    for(LoadedEnviron environ : environCollection) unloadEnviron(environ);
  }

  public void addLoadedEnviron(LoadedEnviron environ) {
    this.loadedEnvirons.put(environ.info().getNamespace(), environ);
    if(this.enabled) enableEnviron(environ);
  }

  public void enableEnvirons() {
    this.enabled = true;

    for(LoadedEnviron environ : this.loadedEnvirons.values()) {
      enableEnviron(environ);
    }
  }

  public void enableEnviron(LoadedEnviron environ) {
    this.enabledEnvirons.put(environ.info().getNamespace(), environ);

    Laby.labyAPI().eventBus().registerListener(instance);
    Laby.labyAPI().eventBus().fire(environ.getClassLoader(), new EnvironEnableEvent(environ, instance));
  }

  @Override
  public boolean isEnabled(String namespace) {
    if(namespace.equals("player-accessories")) return true;

    Optional<LoadedEnviron> environ = getEnviron(namespace);
    if(!environ.isPresent()) return true;

    EnvironConfig config = getMainConfiguration(namespace);
    return (config == null || (config.enabled() != null && config.enabled().get()));
  }

  @Override
  public boolean isEnabled(InstalledEnvironInfo environInfo, boolean backgroundMeta) {
    if(environInfo == null) return true;
    if(backgroundMeta && environInfo.hasMeta(EnvironMeta.BACKGROUND)) return true;

    return isEnabled(environInfo.getNamespace());
  }

  @Override
  public void registerMainConfiguration(String namespace, EnvironConfig environConfig) {
    ConfigProperty<Boolean> enabled = environConfig.enabled();
    if(enabled != null) {
      Laby.fireEvent(new EnvironStateChangeEvent(namespace, null, enabled.get()));
      enabled.addChangeListener((type, oldValue, newValue) -> Laby.fireEvent(new EnvironStateChangeEvent(namespace, oldValue, newValue)));
    }

    this.environConfigs.put(namespace, environConfig);
  }

  @Override
  public boolean hasMainConfiguration(String namespace) {
    return this.environConfigs.containsKey(namespace);
  }

  public EnvironConfig getMainConfiguration(String namespace) {
    return this.environConfigs.get(namespace);
  }

}
