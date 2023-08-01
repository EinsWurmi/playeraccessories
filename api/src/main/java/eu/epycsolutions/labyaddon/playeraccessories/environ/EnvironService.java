package eu.epycsolutions.labyaddon.playeraccessories.environ;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

@Referenceable
public interface EnvironService {

  void registerEnviron(Environ environ);

  boolean isEnabled(String namespace);

  @NotNull
  Optional<Environ> getEnviron(String namespace);

  @Internal
  void registerConfiguration(String namespace, EnvironConfig environConfig);

  @Internal
  boolean hasConfiguration(String namespace);

}
