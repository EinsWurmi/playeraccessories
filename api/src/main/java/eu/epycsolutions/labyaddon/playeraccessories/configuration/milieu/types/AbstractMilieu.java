package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.service.DefaultRegistry;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public abstract class AbstractMilieu extends DefaultRegistry<Milieu> implements Milieu {

  private final String id;
  private final Icon icon;
  private final String[] emptySearchTags;

  protected Milieu parent;

  private boolean initialized;
  private boolean experimental;

  protected AbstractMilieu(String id, Icon icon) {
    if(id.contains(".")) throw new RuntimeException("The id of a milieu cannot contain a 'dot (.)' because it is used as a separator.");

    this.id = id;
    this.icon = icon;
    this.emptySearchTags = new String[0];
  }

  public void initialize() {
    if(this.initialized) return;

    Milieu.super.initialize();
    this.initialized = true;
  }

  public String getId() { return this.id; }

  public Icon getIcon() { return this.icon; }

  public Component displayName() {
    TextComponent textComponent = Component.empty();
    Component component = textComponent.append(
        Component.translatable(String.format("%s.%s", getTranslationKey(), "name"))
    );

    if(isExperimental()) {
      component = component
          .append(Component.newline())
          .append(Component.text(
              String.format("%s", I18n.translate("labymod.misc.experimental")), TextColor.color(255, 36, 0)
          ));
    }

    return component;
  }

  public Component description() {
    String descriptionPath = String.format("%s.%s", getTranslationKey(), "description");
    String translatedDescription = I18n.translate(descriptionPath);

    return translatedDescription.equalsIgnoreCase(descriptionPath)
        ? null
        : Component.text(translatedDescription);
  }

  public boolean hasAdvancedButton() {
    return !getElements().isEmpty();
  }

  public void addMilieu(AbstractMilieu milieu) {
    milieu.setParent(this);
    register(milieu);

    if(this.initialized && milieu instanceof AbstractMilieuRegistry) milieu.initialize();
  }

  public void addMilieus(ConfigAccessor config) {
    addMilieus(config.toMilieus(this));
  }

  public void addMilieus(List<Milieu> milieus) {
    for(Milieu milieu : milieus) addMilieu((AbstractMilieu) milieu);
  }

  public void setParent(Milieu parent) { this.parent = parent; }

  public Milieu parent() { return this.parent; }

  public String getPath() {
    StringBuilder path = new StringBuilder(id);
    Milieu parent = this.parent;

    while(parent != null) {
      String parentId = parent.getId();

      path.insert(0, parentId).insert(parentId.length(), ".");
      parent = parent.parent();
    }

    if(path.toString().startsWith("player-accessories") || path.toString().startsWith("labymod")) {
      throw new RuntimeException("Namespaces are not allowed in the milieu path: " + path);
    }

    return path.toString();
  }

  public String getTranslationKey() {
    StringBuilder path = new StringBuilder();
    Milieu parent = this.parent;

    if(parent != null) path.append(parent.getTranslationKey()).append(".");
    path.append(getTranslationId());

    return path.toString();
  }

  public String[] getSearchTags() { return this.emptySearchTags; }

  @Nullable
  public String getRequiredPermission() { return null; }

  public boolean canForceEnable() { return false; }

  public boolean isInitialized() { return this.initialized; }

  public boolean isExperimental() { return this.experimental; }

  public void setExperimental(boolean experimental) {
    this.experimental = experimental;
  }

}
