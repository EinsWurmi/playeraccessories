package eu.epycsolutions.labyaddon.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.api.generated.ReferenceStorage;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.AccessoriesConfigProvider;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.accessories.AccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.laby.PlayerAccessoriesConfig;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigProvider;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.AbstractMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.RootMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.environ.EnvironService;
import eu.epycsolutions.labyaddon.playeraccessories.events.config.ConfigurationSaveEvent;
import eu.epycsolutions.labyaddon.playeraccessories.gui.navigation.elements.AccessoriesNavigationElement;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.navigation.NavigationRegistry;
import net.labymod.api.event.EventBus;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.scoreboard.TabListUpdateEvent;
import net.labymod.api.models.Implements;
import net.labymod.api.models.addon.annotation.AddonMain;
import javax.inject.Inject;
import javax.inject.Singleton;

@AddonMain
@Singleton
@Implements(AccessoriesAPI.class)
public class PlayerAccessories extends LabyAddon<PlayerAccessoriesConfig> implements AccessoriesAPI {

  private static PlayerAccessories instance;

  private EventBus eventBus;
  private final AbstractMilieuRegistry coreMilieuRegistry = RootMilieuRegistry.playerAccessories("settings").holdable(false);

  private ConfigProvider<AccessoriesConfig> accessoriesConfig;

  private EnvironService environService;

  private WidgetRegistry widgetRegistry;

  @Inject
  public PlayerAccessories() {
    instance = this;
  }

  @Override
  protected void enable() {
    this.coreMilieuRegistry.addMilieus(this.accessoriesConfig.get());

    this.coreMilieuRegistry.initialize();

    // Registry Services
    NavigationRegistry navigationRegistry = this.labyAPI().navigationService();

    navigationRegistry.registerAfter("labymod", "player-accessories", new AccessoriesNavigationElement());
  }

  public void onPreGameStarted() {
    this.logger().info("PlayerAccessories PRE STARTUP");

    // Event Bus
    this.environService = referenceStorage().environService();

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
    labyAPI().eventBus().fire(new TabListUpdateEvent());
  }

  @Override
  protected Class<? extends PlayerAccessoriesConfig> configurationClass() {
    return PlayerAccessoriesConfig.class;
  }

  public static PlayerAccessories instance() {
    return instance;
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
    return this.environService;
  }

  @Override
  public WidgetRegistry widgetRegistry() {
    return this.widgetRegistry;
  }

}
