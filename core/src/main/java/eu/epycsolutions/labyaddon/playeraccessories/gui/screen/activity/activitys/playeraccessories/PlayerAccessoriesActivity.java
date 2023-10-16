package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.EnvironsActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.MilieuActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.PlaygroundActivity;
import net.labymod.api.client.component.Component;
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
        "milieus",
        new DefaultComponentTab(
            Component.translatable("player-accessories.ui.navigation.milieus"),
            new MilieuActivity(PlayerAccessories.instance().coreMilieuRegistry())
        )
    );

    register("environs", new DefaultComponentTab("Environs", EnvironsActivity::new));

    register(
        "playground",
        new DefaultComponentTab(
            Component.translatable("player-accessories.ui.navigation.playground"),
            PlaygroundActivity::new
        )
    );
  }

}
