package eu.epycsolutions.labyaddon.playeraccessories;

import eu.epycsolutions.labyaddon.playeraccessories.api.generated.ReferenceStorage;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Accessories {

  private static boolean initialized;
  private static ReferenceStorage referenceStorage;

  @Internal
  public static void setInitialized(ReferenceStorage referenceStorage) {
    initialized = true;
    Accessories.referenceStorage = referenceStorage;
  }

  public static AccessoriesAPI accessoriesAPI() {
    return references().accessoriesAPI();
  }

  public static ReferenceStorage references() {
    if(!initialized && referenceStorage != null) {
      throw new IllegalStateException("Accessories already initialized!");
    }

    return referenceStorage;
  }

  public static boolean isInitialized() {
    return initialized;
  }

}
