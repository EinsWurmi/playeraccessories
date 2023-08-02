package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigLoadException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigSaveException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.impl.JsonConfigLoader;
import java.io.IOException;

public abstract class ConfigProvider<T extends ConfigAccessor> {

  private T config;
  private ConfigLoader loader;

  protected abstract Class<? extends ConfigAccessor> getType();

  public T load(ConfigLoader loader) {
    try {
      return load(loader, 0);
    } catch(ConfigLoadException exception) {
      exception.printStackTrace();
    }

    return null;
  }

  public T safeLoad(ConfigLoader loader) throws ConfigLoadException {
    return load(loader, 0);
  }

  public T load(ConfigLoader loader, int tries) throws ConfigLoadException {
    if(tries > 3) throw new ConfigLoadException(getType(), null);
    this.loader = loader;

    try {
      return this.config = (T) loader.load(getType());
    } catch(ConfigLoadException exception) {
      try {
        loader.invalidate(getType());
        exception.printStackTrace();
      } catch(IOException ioException) {
        throw exception;
      }
    }

    return load(loader, ++tries);
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
    return this.config;
  }

  public ConfigLoader getLoader() {
    return this.loader;
  }

}
