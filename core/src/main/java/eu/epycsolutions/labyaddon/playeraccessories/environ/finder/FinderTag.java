package eu.epycsolutions.labyaddon.playeraccessories.environ.finder;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import org.spongepowered.include.com.google.gson.annotations.SerializedName;
import java.util.Optional;

public class FinderTag {

  private static final FinderController CONTROLLER = PlayerAccessories.references().finderController();

  private FinderTag parent;
  @SerializedName("parent_category")
  private int parentCategory;

  private int id;

  private String tag;
  private String description;

  public Optional<FinderTag> getParent() {
    return CONTROLLER.getTag(this.parentCategory);
  }

  public String getName() {
    return this.tag;
  }

  public String getDescription() {
    return this.description;
  }

  public int getId() {
    return this.id;
  }

  public int getParentCategory() {
    return this.parentCategory;
  }

  public String toString() {
    return String.valueOf(getId());
  }

}
