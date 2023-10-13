package eu.epycsolutions.labyaddon.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.api.generated.ReferenceStorage;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.AccessoriesConfigProvider;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.AccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.laby.PlayerAccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigProvider;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.AbstractMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.RootMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.core.generated.DefaultReferenceStorage;
import eu.epycsolutions.labyaddon.playeraccessories.environ.DefaultEnvironService;
import eu.epycsolutions.labyaddon.playeraccessories.environ.EnvironService;
import eu.epycsolutions.labyaddon.playeraccessories.events.config.ConfigurationSaveEvent;
import eu.epycsolutions.labyaddon.playeraccessories.gui.navigation.elements.AccessoriesNavigationElement;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.PlayerAccessoriesActivity;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.activitys.playeraccessories.child.MilieuActivity;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.navigation.NavigationRegistry;
import net.labymod.api.client.gui.navigation.elements.ScreenNavigationElement;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.playerlist.PlayerListUpdateEvent;
import net.labymod.api.models.Implements;
import net.labymod.api.models.addon.annotation.AddonMain;
import javax.inject.Inject;
import javax.inject.Singleton;

@AddonMain
@Singleton
@Implements(AccessoriesAPI.class)
public class PlayerAccessories extends LabyAddon<PlayerAccessoriesConfig> implements AccessoriesAPI {

  private static AccessoriesAPI instance;

  private final AbstractMilieuRegistry coreMilieuRegistry = RootMilieuRegistry.playerAccessories("settings").holdable(false);

  private ConfigProvider<AccessoriesConfig> accessoriesConfig;

  private WidgetRegistry widgetRegistry;

  @Inject
  public PlayerAccessories() {
    instance = this;
  }

  @Override
  protected void enable() {
    // DISABLED CAUSE OF CRASH FROM CREATING CONFIG
    // this.coreMilieuRegistry.addMilieus(this.accessoriesConfig.get());
    this.coreMilieuRegistry.initialize();

    // Registry Services
    NavigationRegistry navigationRegistry = this.labyAPI().navigationService();

    navigationRegistry.registerAfter("labymod", "player-accessories", new AccessoriesNavigationElement());
  }

  public void onPreGameStarted() {
    this.logger().info("PlayerAccessories PRE STARTUP");

    Accessories.setInitialized(this.referenceStorage());

    ((DefaultEnvironService) environService()).enableEnvirons();


    this.widgetRegistry = referenceStorage().widgetRegistry();

    // Setting Registry
    this.accessoriesConfig = AccessoriesConfigProvider.INSTANCE;
    this.accessoriesConfig.loadJson();
  }

  @Subscribe
  public void onConfigurationSave(ConfigurationSaveEvent event) {
    this.accessoriesConfig.save();
  }

  public void reloadTabList() {
    labyAPI().eventBus().fire(new PlayerListUpdateEvent());
  }

  @Override
  protected Class<? extends PlayerAccessoriesConfig> configurationClass() {
    return PlayerAccessoriesConfig.class;
  }

  public static PlayerAccessories instance() {
    if(instance == null) instance = Accessories.references().accessoriesAPI();
    return (PlayerAccessories) instance;
  }

  @Override
  public AbstractMilieuRegistry coreMilieuRegistry() {
    return this.coreMilieuRegistry;
  }

  @Override
  public ReferenceStorage referenceStorage() {
    return referenceStorageAccessor();
  }

  @Override
  public AccessoriesConfig accessoriesConfig() {
    return this.accessoriesConfig.get();
  }

  @Override
  public EnvironService environService() {
    return DefaultEnvironService.getInstance();
  }

  @Override
  public WidgetRegistry widgetRegistry() {
    return this.widgetRegistry;
  }

  @Override
  public void showMilieu(Milieu milieu) {
    NavigationRegistry navigationRegistry = labyAPI().navigationService();
    ScreenNavigationElement navigationElement = (ScreenNavigationElement) navigationRegistry.getById("player-accessories");

    PlayerAccessoriesActivity playerAccessoriesActivity = (PlayerAccessoriesActivity) navigationElement.getScreen();
    MilieuActivity milieuActivity = (MilieuActivity) playerAccessoriesActivity.switchTab("milieus");

    if(milieuActivity != null) {
      if(!milieu.isInitialized()) milieu.initialize();
      milieuActivity.setSelectedMilieu(milieu);
    }

    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(playerAccessoriesActivity);
  }

  public static DefaultReferenceStorage references() {
    return (DefaultReferenceStorage) Accessories.references();
  }

}
