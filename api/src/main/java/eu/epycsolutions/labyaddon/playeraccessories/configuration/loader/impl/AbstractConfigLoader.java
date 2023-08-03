package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.impl;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.Constants.AccessoriesFiles;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigLoader;
import net.labymod.api.Laby;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractConfigLoader implements ConfigLoader {

  protected final Path directory;
  protected final Map<String, String> variables = new HashMap<>();

  public AbstractConfigLoader(Path directory) {
    this.directory = directory;
  }

  public Path getPath(Class<? extends ConfigAccessor> clazz) {
    String namespace = Laby.labyAPI().getNamespace(clazz);

    return directory
        .resolve(namespace)
        .resolve(String.format("%s.%s", replaceVariables(getRelativePath(clazz)), getFileExtension()));
  }

  @Override
  public void setVariable(String key, Object value) {
    this.variables.put(key, value.toString());
  }

  protected String replaceVariables(String configPath) {
    for(Entry<String, String> entry : this.variables.entrySet()) {
      configPath = configPath.replace(entry.getKey(), entry.getValue());
    }

    return configPath;
  }

  public static Path defaultDirectory() {
    return AccessoriesFiles.PLAYERACCESSORIES_ADDON_CONFIG;
  }

  @Override
  public void invalidate(Class<? extends ConfigAccessor> type) throws IOException {
    Path path = getPath(type);
    Path invalidPath = path.getParent().resolve(path.getFileName().toString() + ".invalid");

    Files.copy(path, invalidPath, StandardCopyOption.REPLACE_EXISTING);
    Files.deleteIfExists(path);
  }
}
