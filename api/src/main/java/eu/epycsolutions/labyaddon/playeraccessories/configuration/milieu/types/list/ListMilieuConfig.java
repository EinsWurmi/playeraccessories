package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.list;

import net.labymod.api.client.component.Component;
import org.jetbrains.annotations.NotNull;

public interface ListMilieuConfig {

  @NotNull
  default Component entryDisplayName() {
    return ListMilieu.defaultEntryName();
  }

  @NotNull
  default Component newEntryTitle() {
    return Component.translatable("player-accessories.ui.milieu.list.entry");
  }

  default boolean isInvalid() {
    return false;
  }

}
