package eu.epycsolutions.labyaddon.playeraccessories.events.config;

import com.google.gson.JsonObject;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import net.labymod.api.event.Event;
import net.labymod.api.util.GsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class ConfigurationVersionUpdateEvent implements Event {

  private final Class<? extends Config> configClass;

  private final int usedVersion;
  private final int intendedVersion;

  private final JsonObject jsonObject;
  private JsonObject editedJsonObject;

  public ConfigurationVersionUpdateEvent(
      @NotNull Class<? extends Config> configClass,
      @NotNull JsonObject jsonObject,
      int usedVersion,
      int intendedVersion
  ) {
    Objects.requireNonNull(configClass, "configClass");
    Objects.requireNonNull(jsonObject, "jsonObject");

    this.configClass = configClass;
    this.jsonObject = GsonUtil.copy(jsonObject);

    this.usedVersion = usedVersion;
    this.intendedVersion = intendedVersion;
  }

  public @NotNull Class<? extends Config> getConfigClass() {
    return this.configClass;
  }

  public int getUsedVersion() {
    return this.usedVersion;
  }

  public int getIntendedVersion() {
    return this.intendedVersion;
  }

  public @NotNull JsonObject getJsonObject() {
    return this.jsonObject;
  }

  public void setJsonObject(@NotNull JsonObject jsonObject) {
    Objects.requireNonNull(jsonObject, "jsonObject");
    this.editedJsonObject = jsonObject;
  }

  public @Nullable JsonObject getEditedJsonObject() {
    return this.editedJsonObject;
  }

}
