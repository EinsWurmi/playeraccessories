package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types;

import net.labymod.api.client.component.Component;
import net.labymod.api.util.I18n;
import java.util.ArrayList;
import java.util.List;

public class MilieuHeader extends AbstractMilieu {

  private final String translationId;
  private final String customTranslation;

  private final boolean center;

  public MilieuHeader(String id, boolean center, String customTranslation, String translationId) {
    super(id, null);

    this.center = center;
    this.translationId = translationId;
    this.customTranslation = customTranslation;
  }

  @Override
  public String getTranslationId() {
    return "header." + this.translationId;
  }

  @Override
  public String getTranslationKey() {
    return this.customTranslation.isEmpty()
          ? super.getTranslationKey()
          : (this.customTranslation + "." + this.customTranslation);
  }

  public boolean isCenter() {
    return this.center;
  }

  public List<Component> getRows() {
    String translationKey = getTranslationKey();
    String defaultTranslationKey = translationKey + ".name";
    String defaultTranslation = I18n.getTranslation(defaultTranslationKey);

    List<Component> components = new ArrayList<>();
    if(defaultTranslation != null) {
      String[] split = defaultTranslation.split("\\n");
      for(String row : split) components.add(Component.text(row));
    }

    return components;
  }

}
