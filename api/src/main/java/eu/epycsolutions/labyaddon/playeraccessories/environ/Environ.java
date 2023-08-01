package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.client.gui.screen.widget.Widget;
import org.jetbrains.annotations.ApiStatus.Internal;
import java.util.ArrayList;
import java.util.List;

public class Environ {

  private final InstalledEnvironInfo info;
  private final List<Widget> milieus = new ArrayList<>();

  private final EnvironConfig environConfig;

  @Internal
  public Environ(InstalledEnvironInfo info, EnvironConfig environConfig) {
    this.info = info;
    this.environConfig = environConfig;
  }

  public InstalledEnvironInfo info() {
    return this.info;
  }

  public EnvironConfig config() {
    return this.environConfig;
  }

  public List<Widget> getMilieus() {
    return this.milieus;
  }

}
