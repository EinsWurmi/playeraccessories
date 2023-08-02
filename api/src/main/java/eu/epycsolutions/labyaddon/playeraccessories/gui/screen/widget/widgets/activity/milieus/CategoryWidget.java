package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.activity.milieus;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;

@AutoWidget
public class CategoryWidget extends ButtonWidget {

  private final Milieu category;

  public CategoryWidget(Milieu category) {
    this.category = category;
    this.component = category.displayName();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
  }

  public Milieu category() {
    return this.category;
  }

}
