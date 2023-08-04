package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types;

import eu.epycsolutions.labyaddon.playeraccessories.Accessories;
import eu.epycsolutions.labyaddon.playeraccessories.AccessoriesAPI;
import eu.epycsolutions.labyaddon.playeraccessories.environ.EnvironConfig;
import eu.epycsolutions.labyaddon.playeraccessories.environ.EnvironService;
import org.jetbrains.annotations.ApiStatus.Internal;

public class RootMilieuRegistry extends AbstractMilieuRegistry {

  private final String namespace;
  private String translationId;

  protected RootMilieuRegistry(String namespace, String id) {
    super(id, null);

    this.namespace = namespace;
    this.translationId = id;
  }

  public RootMilieuRegistry translationId(String translationId) {
    this.translationId = translationId;
    return this;
  }

  public boolean isEnviron() {
    return !this.namespace.equals(AccessoriesAPI.ADDON_MAIN_NAMESPACE);
  }

  public String getNamespace() {
    return this.namespace;
  }

  @Override
  public String getTranslationId() {
    return this.translationId;
  }

  public static <T extends EnvironConfig> RootMilieuRegistry environ(Object namespace, T config) {
    AccessoriesAPI accessoriesAPI = Accessories.accessoriesAPI();
    EnvironService environService = accessoriesAPI.environService();

    if(environService.hasMainConfiguration((String) namespace)) throw new UnsupportedOperationException("Environ already has a main configuration");

    RootMilieuRegistry milieuRegistry = (new RootMilieuRegistry((String) namespace,
        (String) namespace)).translationId("settings");
    milieuRegistry.addMilieus(config);
    environService.registerMainConfiguration((String) namespace, config);

    return milieuRegistry;
  }

  @Internal
  public static RootMilieuRegistry playerAccessories(String id) {
    return new RootMilieuRegistry(AccessoriesAPI.ADDON_MAIN_NAMESPACE, id);
  }

  @Internal
  public static RootMilieuRegistry custom(String namespace, String id) {
    return new RootMilieuRegistry(namespace, id);
  }

  @Override
  public String getTranslationKey() {
    return this.namespace + ".ui.milieus." + this.namespace;
  }

}
