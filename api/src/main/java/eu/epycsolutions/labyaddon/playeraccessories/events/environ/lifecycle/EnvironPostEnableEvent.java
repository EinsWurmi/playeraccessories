package eu.epycsolutions.labyaddon.playeraccessories.events.environ.lifecycle;

import eu.epycsolutions.labyaddon.playeraccessories.environ.LoadedEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@LabyEvent(background = true, classLoaderExclusive = true, allowAllExceptions = true)
public record EnvironPostEnableEvent(LoadedEnviron environ) implements Event {

  public EnvironPostEnableEvent(@NotNull LoadedEnviron environ) {
    Objects.requireNonNull(environ, "Loaded environ cannot be null!");

    this.environ = environ;
  }

  public InstalledEnvironInfo environInfo() {
    return this.environ.info();
  }

}
