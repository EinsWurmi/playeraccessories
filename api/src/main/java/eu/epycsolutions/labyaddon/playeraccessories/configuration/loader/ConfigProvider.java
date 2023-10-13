package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigLoadException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigSaveException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.impl.JsonConfigLoader;
import net.labymod.api.util.logging.Logging;
import java.io.IOException;

public abstract class ConfigProvider<T extends ConfigAccessor> {

  private static final Logging LOGGER = Logging.create(ConfigProvider.class);

  private T config;
  private ConfigLoader loader;

  protected abstract Class<? extends ConfigAccessor> getType();

  public T load(ConfigLoader loader) {
    try {
      return load(loader, 0);
    } catch (ConfigLoadException exception) {
      exception.printStackTrace();
    }

    return null;
  }

  public T safeLoad(ConfigLoader loader) throws ConfigLoadException {
    return load(loader, 0);
  }

  @SuppressWarnings("unchecked")
  public T load(ConfigLoader loader, int tries) throws ConfigLoadException {
    if (tries > 3) {
      throw new ConfigLoadException(getType(), null);
    }

    this.loader = loader;

    T loaded;
    try {
      loaded = (T) loader.load(getType());
    } catch (ConfigLoadException exception) {
      try {
        loader.invalidate(getType());
        exception.printStackTrace();
      } catch (IOException ioException) {
        throw exception;
      }

      loaded = load(loader, ++tries);
    }

    return this.config = loaded;
  }

  public T loadJson() {
    return load(JsonConfigLoader.createDefault());
  }

  public boolean save() {
    try {
      this.loader.save(this.config);
      return true;
    } catch(Exception exception) {
      exception.printStackTrace();
    }

    return false;
  }

  public boolean safeSave() throws ConfigSaveException {
    this.loader.save(this.config);
    return true;
  }

  public Object serialize() {
    try {
      return this.loader.serialize(this.config);
    } catch(Exception exception) {
      exception.printStackTrace();
    }

    return null;
  }

  public T get() {
    if(this.config == null) {
      LOGGER.error(
          getType().getSimpleName() + " is null. Loader is" +
              (this.loader == null ? "null" : "present")
      );
    }

    return this.config;
  }

  public ConfigLoader getLoader() {
    return this.loader;
  }

}
