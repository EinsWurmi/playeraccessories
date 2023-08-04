package eu.epycsolutions.labyaddon.playeraccessories.events.environ.lifecycle;

import eu.epycsolutions.labyaddon.playeraccessories.environ.LoadedEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.event.Event;
import net.labymod.api.event.LabyEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@LabyEvent(background = true, classLoaderExclusive = true, allowAllExceptions = true)
public record EnvironEnableEvent(LoadedEnviron environ, Object instance) implements Event {

  public EnvironEnableEvent(@NotNull LoadedEnviron environ, @NotNull Object instance) {
    Objects.requireNonNull(environ, "Loaded environ cannot be null!");
    Objects.requireNonNull(instance, "Environ instance cannot be null!");

    this.environ = environ;
    this.instance = instance;
  }

  public InstalledEnvironInfo environInfo() {
    return this.environ.info();
  }

}
