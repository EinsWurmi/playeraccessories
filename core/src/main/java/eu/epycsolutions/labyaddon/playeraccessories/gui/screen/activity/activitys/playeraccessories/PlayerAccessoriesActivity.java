package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.MilieuActivity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.Links;
import net.labymod.api.client.gui.screen.activity.types.TabbedActivity;
import net.labymod.api.client.gui.screen.widget.widgets.navigation.tab.DefaultComponentTab;

@AutoActivity
@Links({ @Link("activity/accessories.lss"), @Link("activity/milieus.lss") })
public class PlayerAccessoriesActivity extends TabbedActivity {

  public PlayerAccessoriesActivity() {
    register(
        "settings",
        new DefaultComponentTab("PlayerAccessories", new MilieuActivity(PlayerAccessories.instance().coreMilieuRegistry()))
    );
  }

}
