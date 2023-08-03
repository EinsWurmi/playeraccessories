package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types;

import eu.epycsolutions.labyaddon.playeraccessories.api.generated.ReferenceStorage;
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
    return !this.namespace.equals("player-accessories");
  }

  public String getNamespace() {
    return this.namespace;
  }

  public String getTranslationId() {
    return this.translationId;
  }

  public static <T extends EnvironConfig> RootMilieuRegistry environ(String namespace, T config) {
    ReferenceStorage referenceStorage = new ReferenceStorage();
    EnvironService environService = referenceStorage.environService();

    if(environService.hasConfiguration(namespace)) throw new UnsupportedOperationException("Environ already has a main configuration");

    RootMilieuRegistry milieuRegistry = (new RootMilieuRegistry(namespace, namespace)).translationId("settings");
    milieuRegistry.addMilieus(config);
    environService.registerConfiguration(namespace, config);

    return milieuRegistry;
  }

  @Internal
  public static RootMilieuRegistry playerAccessories(String id) {
    return new RootMilieuRegistry("player-accessories", id);
  }

  @Internal
  public static RootMilieuRegistry custom(String namespace, String id) {
    return new RootMilieuRegistry(namespace, id);
  }

  public String getTranslationKey() {
    return this.namespace + ".ui.milieus." + this.namespace;
  }

}
