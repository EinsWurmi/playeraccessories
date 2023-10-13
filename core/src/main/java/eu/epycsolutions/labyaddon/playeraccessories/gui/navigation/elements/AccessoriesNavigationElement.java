package eu.epycsolutions.labyaddon.playeraccessories.gui.navigation.elements;

import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.PlayerAccessoriesActivity;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.navigation.elements.ScreenNavigationElement;

public class AccessoriesNavigationElement extends ScreenNavigationElement {

  public AccessoriesNavigationElement() {
    super(new PlayerAccessoriesActivity());
  }

  @Override
  public String getWidgetId() {
    return "player-accessories";
  }

  @Override
  public Component getDisplayName() {
    return Component.text("PlayerAccessories");
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public boolean shouldDocumentHandleEscape() {
    return true;
  }
}
