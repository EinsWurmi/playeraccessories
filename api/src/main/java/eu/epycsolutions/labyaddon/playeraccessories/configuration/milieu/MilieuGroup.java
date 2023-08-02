package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.service.DefaultRegistry;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class MilieuGroup extends DefaultRegistry<Milieu> implements Milieu {

  private final String[] emptySearchTags;
  private Icon icon;

  private Component displayName;
  private final Component description = Component.empty();

  private boolean experimental;
  private boolean filtered;

  public MilieuGroup() {
    this.emptySearchTags = new String[0];
  }

  public void addMilieu(Milieu milieu) {
    register(milieu);
  }

  public void addMilieus(List<Milieu> milieus) {
    register(milieus);
  }

  public MilieuGroup of(List<Milieu> milieus) {
    register(milieus);
    return this;
  }

  public MilieuGroup filtered(boolean filtered) {
    this.filtered = filtered;
    return this;
  }

  public Icon getIcon() {
    return this.icon;
  }

  public Component displayName() {
    return this.displayName;
  }

  public Component description() {
    return this.description;
  }

  public boolean hasAdvancedButton() {
    return false;
  }

  public Milieu parent() {
    return null;
  }

  public String getPath() {
    return getId();
  }

  public String getTranslationKey() {
    return getId();
  }

  public String[] getSearchTags() {
    return this.emptySearchTags;
  }

  @Nullable
  public String getRequiredPermission() {
    return null;
  }

  public boolean canForceEnable() {
    return false;
  }

  public boolean isInitialized() {
    return true;
  }

  public boolean isExperimental() {
    return this.experimental;
  }

  public void setExperimental(boolean experimental) {
    this.experimental = experimental;
  }

  public boolean isFiltered() {
    return this.filtered;
  }

  public String getId() {
    return "group";
  }

  public static MilieuGroup named(Component component) {
    MilieuGroup group = new MilieuGroup();
    group.displayName = component;

    return group;
  }

  public static MilieuGroup named(Component component, Icon icon) {
    MilieuGroup group = new MilieuGroup();
    group.displayName = component;
    group.icon = icon;

    return group;
  }

  public static MilieuGroup empty() {
    return new MilieuGroup();
  }

}
