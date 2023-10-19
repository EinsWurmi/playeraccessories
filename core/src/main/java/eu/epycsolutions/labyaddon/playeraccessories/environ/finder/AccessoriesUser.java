package eu.epycsolutions.labyaddon.playeraccessories.environ.finder;

import net.labymod.api.client.gui.icon.Icon;
import org.spongepowered.include.com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class AccessoriesUser {

  private UUID uuid;

  @SerializedName("user_name")
  private String userName;

  public String getUserName() {
    return this.userName;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public Icon icon() {
    return Icon.head(this.userName);
  }

}
