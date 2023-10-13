package eu.epycsolutions.labyaddon.playeraccessories.environ;

import eu.epycsolutions.labyaddon.playeraccessories.Accessories;
import eu.epycsolutions.labyaddon.playeraccessories.AccessoriesAPI;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigLoadException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.expections.ConfigSaveException;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigLoader;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.ConfigProvider;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.impl.JsonConfigLoader;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.RootMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.environ.exception.EnvironInvalidException;
import eu.epycsolutions.labyaddon.playeraccessories.events.config.ConfigurationSaveEvent;
import eu.epycsolutions.labyaddon.playeraccessories.events.environ.lifecycle.EnvironEnableEvent;
import eu.epycsolutions.labyaddon.playeraccessories.events.environ.lifecycle.EnvironPostEnableEvent;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.InstalledEnvironInfo;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.configuration.converter.LegacyConverter;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.logging.Logging;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AccessoriesEnviron<T extends EnvironConfig> {

  private final Logging logging = Logging.create(getClass());

  private final Map<Class<? extends ConfigAccessor>, AccessoriesEnvironConfigProvider<?>> customConfigProviders = new HashMap<>();
  private final AccessoriesEnvironConfigProvider<? extends T> configProvider;

  private final AccessoriesAPI accessoriesAPI = Accessories.accessoriesAPI();
  private final LabyAPI labyAPI = Laby.labyAPI();

  private AccessoriesEnviron<T> instance;

  private InstalledEnvironInfo environInfo;

  private boolean registeredCategory;
  private boolean loadedInRuntime;

  protected AccessoriesEnviron() {
    if(configurationClass() == null) environExceptionMessage("The environs configuration class is null!");
    preConfigurationLoad();

    this.configProvider = new AccessoriesEnvironConfigProvider<>(configurationClass());
    this.configProvider.safeLoad(JsonConfigLoader.createDefault());

    if(configuration().enabled() == null) environExceptionMessage("The environ has to implement the enable milieu inherited by MilieuConfig");
  }

  public final T configuration() {
    return this.configProvider.get();
  }

  public final AccessoriesAPI accessoriesAPI() {
    return this.accessoriesAPI;
  }

  public final LabyAPI labyAPI() {
    return this.labyAPI;
  }

  public final Logging logger() {
    return this.logging;
  }

  public final InstalledEnvironInfo environInfo() {
    return this.environInfo;
  }

  public final boolean wasLoadedInRuntime() {
    return this.loadedInRuntime;
  }

  public final void sendMessage(String message) {
    this.labyAPI.minecraft().chatExecutor().chat(message);
  }

  public final void displayMessage(String message) {
    displayMessage(Component.text(message));
  }

  public final void displayMessage(Component message) {
    this.labyAPI.minecraft().chatExecutor().displayClientMessage(message);
  }

  protected void preConfigurationLoad() { }

  protected void load() { }

  protected abstract void enable();

  protected abstract Class<? extends T> configurationClass();

  protected final void registerMilieuCategory() {
    if(this.registeredCategory) environExceptionMessage("Cannot register the same category twice");

    T configuration = configuration();
    if(configuration == null) environExceptionMessage("Cannot register the category because config is null");

    RootMilieuRegistry registry = RootMilieuRegistry.environ(this, configuration);
    this.accessoriesAPI.coreMilieuRegistry().addMilieu(registry);

    this.registeredCategory = true;
  }


  protected final void registerListener(@NotNull Object listener) {
    Objects.requireNonNull(listener, "Listener");
    this.labyAPI.eventBus().registerListener(listener);
  }

  protected final void registerLegacyConverted(@NotNull LegacyConverter<?> legacyConverter) {
    Objects.requireNonNull(legacyConverter, "LegacyConverter");
    Laby.references().legacyConfigConverter().register(legacyConverter);
  }

  protected final void registerCommand(@NotNull Command command) {
    Objects.requireNonNull(command, "Command");
    this.labyAPI.commandService().register(command);
  }


  public final void saveConfiguration() throws ConfigSaveException {
    this.configProvider.safeSave();
  }

  protected final <C extends ConfigAccessor> C addCustomConfiguration(@NotNull Class<C> configurationClass) throws ConfigLoadException {
    Objects.requireNonNull(configurationClass, "Custom Configuration Class");
    Class<? extends T> mainConfigurationClass = configurationClass();

    if(mainConfigurationClass == configurationClass) environExceptionMessage("Cannot add the main environ config as a custom config");
    if(this.customConfigProviders.containsKey(configurationClass)) environExceptionMessage("The custom config %s was already loaded!", configurationClass.getName());

    String mainConfigurationName = ConfigLoader.getName(mainConfigurationClass);
    if(mainConfigurationName.equals(ConfigLoader.getName(configurationClass))) environExceptionMessage("Config %s has the same ConfigName as the main config", configurationClass.getName());

    for(Class<? extends ConfigAccessor> value : this.customConfigProviders.keySet()) {
      environExceptionMessage("Config %s has the same ConfigName as the custom config %s", configurationClass.getName(), value);
      break;
    }

    AccessoriesEnvironConfigProvider<C> customConfigProvider = new AccessoriesEnvironConfigProvider<>(configurationClass);
    ConfigAccessor configAccessor = customConfigProvider.safeLoad(JsonConfigLoader.createDefault());
    this.customConfigProviders.put(configurationClass, customConfigProvider);

    return (C) configAccessor;
  }

  protected final <C extends ConfigAccessor> void saveCustomConfiguration(@NotNull Class<C> configurationClass) throws ConfigSaveException {
    Objects.requireNonNull(configurationClass, "Custom Configuration Class");
    ConfigProvider<?> customConfigProvider = this.customConfigProviders.get(configurationClass);
    if(customConfigProvider == null) environExceptionMessage("Cannot save the custom config %s as it isn't declared as an custom config", configurationClass.getName());

    customConfigProvider.safeSave();
  }


  @Subscribe
  public final void onEnvironLoad(EnvironEnableEvent event) {
    this.loadedInRuntime = this.labyAPI.isFullyInitialized();
    this.environInfo = event.environ().info();

    this.instance = (AccessoriesEnviron<T>) event.instance();

    try {
      load();
    } catch(Exception exception) {
      environException("Failed to load the environ.", exception);
    }
  }

  @Subscribe
  public final void onEnvironInitialize(EnvironPostEnableEvent event) {
    try {
      enable();
    } catch(Exception exception) {
      environException("Failed to enable the environ.", exception);
    }
  }

  @Subscribe
  public final void onEnvironMilieusSave(ConfigurationSaveEvent event) {
    saveConfiguration();
  }


  private void environExceptionMessage(String message, Object... arguments) {
    message = String.format(message, arguments);
    environException(message, new EnvironInvalidException(message));
  }

  private void environException(String message, Exception exception) throws RuntimeException {
    if(this.labyAPI.labyModLoader().isAddonDevelopmentEnvironment() && this.environInfo == null) {
      this.labyAPI.minecraft().crashGame(message, exception);
      return;
    }

    throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new RuntimeException(exception);
  }

  @NotNull
  protected final <A extends AccessoriesEnviron<T>> A environInstance() {
    if(this.instance == null) throw new NullPointerException("Environ instance is not initialized yet.");
    return (A) this.instance;
  }


  public class AccessoriesEnvironConfigProvider<C extends ConfigAccessor> extends ConfigProvider<C> {
    private final Class<C> configClass;

    private AccessoriesEnvironConfigProvider(Class<C> configClass) {
      this.configClass = configClass;
    }

    protected Class<C> getType() {
      return this.configClass;
    }

  }

}
