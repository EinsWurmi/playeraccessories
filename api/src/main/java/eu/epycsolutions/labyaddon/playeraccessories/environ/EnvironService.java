package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Optional;

public interface EnvironService {

  @NotNull
  Optional<LoadedEnviron> getEnviron(String namespace);

  Collection<LoadedEnviron> getEnabledEnvirons();

  Collection<LoadedEnviron> getLoadedEnvirons();

  Collection<LoadedEnviron> getVisibleEnvirons();

  @Nullable
  InstalledEnvironInfo getEnvironInfo(String id);

  boolean isEnabled(String namespace);

  boolean isEnabled(InstalledEnvironInfo environInfo, boolean backgroundMeta);

  @Internal
  void registerMainConfiguration(String namespace, EnvironConfig environConfig);

  @Internal
  boolean hasMainConfiguration(String namespace);

}
