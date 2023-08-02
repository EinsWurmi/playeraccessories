package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widgets;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;

public class MilieuHeaderWidget extends AbstractWidget<Widget> {

  private final Component displayName;

  public MilieuHeaderWidget(Component displayName) {
    this.displayName = displayName;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    ComponentWidget header = ComponentWidget.component(this.displayName);
    header.addId("header-title");

    addChild(header);
  }
}
