package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Optional;

@Referenceable
public interface EnvironService {

  @NotNull
  Optional<LoadedEnviron> getEnviron(String namespace);

  Optional<LoadedEnviron> getOptionalEnviron(String id);

  @NotNull
  default Optional<LoadedEnviron> getEnviron(@NotNull Class<?> clazz) {
    return this.getEnviron(clazz.getClassLoader());
  }

  @NotNull
  Optional<LoadedEnviron> getEnviron(ClassLoader classLoader);

  Collection<LoadedEnviron> getLoadedEnvirons();

  Collection<LoadedEnviron> getVisibleEnvirons();

  @Nullable
  InstalledEnvironInfo getEnvironInfo(String id);

  Class<?> loadClassFromEnviron(String name) throws ClassNotFoundException;

  LoadedEnviron getLastCallerEnviron();

  @Nullable
  String getClassPathEnviron();

  boolean isEnabled(Object instance);

  boolean isEnabled(Class<?> clazz);

  boolean isEnabled(String namespace);

  boolean isEnabled(InstalledEnvironInfo environInfo, boolean backgroundMeta);

  @Internal
  void registerMainConfiguration(String namespace, EnvironConfig environConfig);

  @Internal
  boolean hasMainConfiguration(String namespace);

}
