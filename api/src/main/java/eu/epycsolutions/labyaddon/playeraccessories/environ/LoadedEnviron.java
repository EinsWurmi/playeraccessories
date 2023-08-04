package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.environ.transformer.LoadedEnvironClassTransformer;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.client.gui.screen.widget.Widget;
import org.jetbrains.annotations.ApiStatus.Internal;
import java.util.ArrayList;
import java.util.List;

public class LoadedEnviron {

  private final ClassLoader classLoader;
  private final Class<?> mainClass;

  private final List<LoadedEnvironClassTransformer> transformers;

  private final InstalledEnvironInfo info;

  private final List<Widget> milieus = new ArrayList<>();

  private final boolean classPath;

  public LoadedEnviron(ClassLoader classLoader, Class<?> mainClass, List<LoadedEnvironClassTransformer> transformers, InstalledEnvironInfo info) {
    this(classLoader, mainClass, transformers, info, false);
  }

  @Internal
  public LoadedEnviron(ClassLoader classLoader, Class<?> mainClass, List<LoadedEnvironClassTransformer> transformers, InstalledEnvironInfo info, boolean classPath) {
    this.classLoader = classLoader;
    this.mainClass = mainClass;

    this.transformers = transformers;

    this.info = info;

    this.classPath = classPath;
  }

  public ClassLoader getClassLoader() {
    return this.classLoader;
  }

  public Class<?> getMainClass() {
    return this.mainClass;
  }

  public List<LoadedEnvironClassTransformer> getTransformers() {
    return this.transformers;
  }

  public InstalledEnvironInfo info() {
    return this.info;
  }

  public List<Widget> getMilieus() {
    return this.milieus;
  }

  @Internal
  public boolean isClassPath() {
    return this.classPath;
  }

}
