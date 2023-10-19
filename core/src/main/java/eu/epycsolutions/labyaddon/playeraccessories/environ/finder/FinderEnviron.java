package eu.epycsolutions.labyaddon.playeraccessories.environ.finder;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import eu.epycsolutions.labyaddon.playeraccessories.builddata.BuildData;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.Constants.Urls;
import eu.epycsolutions.labyaddon.playeraccessories.environ.DefaultEnvironService;
import eu.epycsolutions.labyaddon.playeraccessories.environ.LoadedEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.EnvironMeta;
import net.labymod.api.Laby;
import net.labymod.api.models.version.VersionCompatibility;
import net.labymod.api.util.I18n;
import net.labymod.api.util.io.web.result.ResultCallback;
import net.labymod.api.util.markdown.MarkdownDocument;
import net.labymod.api.util.version.SemanticVersion;
import org.spongepowered.include.com.google.common.collect.Lists;
import org.spongepowered.include.com.google.common.collect.Sets;
import org.spongepowered.include.com.google.gson.annotations.SerializedName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FinderEnviron {

  private static final FinderController CONTROLLER = PlayerAccessories.references().finderController();

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

  protected Set<FinderTag> finderTags;

  protected Image icon;

  protected Image thumbnail;

  protected Image[] sliderImages;

  protected int id;

  protected String namespace;

  protected String name;

  protected String changelog;

  protected String license;

  protected String author;

  protected boolean featured;

  protected int version;
  protected int downloads;
  protected int ranking;
  protected int releases;

  protected int[] tags;

  protected Rating rating;

  protected EnvironMeta[] meta;
  protected String[] permissions;

  @SerializedName("short_description")
  protected String shortDescription;

  @SerializedName("version_string")
  protected String versionString;

  @SerializedName("downloads_string")
  protected String downloadsString;

  @SerializedName("brand_images")
  protected Image[] brandImages;

  @SerializedName("last_update")
  protected long lastUpdate;

  @SerializedName("required_labymod_build")
  protected int requiredLabyModBuild;

  @SerializedName("required_playeraccessories_build")
  protected int requiredPlayerAccessoriesBuild;
  @SerializedName("implemented_playeraccessories_build")
  protected int implementedPlayerAccessoriesBuild;

  private transient String niceVersionString;

  private transient FinderPermission[] finderPermissions;

  private transient MarkdownDocument description;

  public Rating getRating() {
    return this.rating;
  }

  public String getNamespace() {
    return this.namespace;
  }

  public String getName() {
    return this.name;
  }

  public String getShortDescription() {
    return this.shortDescription;
  }

  public boolean isLabyModCompatible() {
    return (this.requiredLabyModBuild <= BuildData.getBuildNumber());
  }

  public String getVersionString() {
    if(this.niceVersionString == null && this.versionString != null) {
      if(this.versionString.equals("*")) {
        String translation = I18n.getTranslation("player-accessories.environs.allVersions");
        this.niceVersionString = (translation == null) ? this.versionString : translation;

        return this.niceVersionString;
      }

      String[] ranges = this.versionString.split(",");
      StringBuilder stringBuilder = new StringBuilder();

      for(int i = 0; i < ranges.length; i++) {
        if(i != 0) {
          stringBuilder.append(", ");
        }

        String range = ranges[i];
        String[] versions = range.split("<");

        if(versions.length == 1) {
          stringBuilder.append(versions[0]);
        } else {
          if(versions[0].equals("*")) {
            stringBuilder.append("1.8.9");
          } else {
            stringBuilder.append(versions[0]);
          }

          if(versions[1].equals("*")) {
            stringBuilder.append("+");
          } else {
            stringBuilder.append(" - ").append(versions[1]);
          }
        }
      }

      this.niceVersionString = stringBuilder.toString();
      return this.niceVersionString;
    }

    return (this.niceVersionString != null) ? this.niceVersionString : this.versionString;
  }

  public String getChangelog() {
    return this.changelog;
  }

  public String getLicense() {
    if(this.license == null) return null;
    return this.license.replace("-", " ");
  }

  public String getDownloadsString() {
    return (this.downloadsString == null) ? "?" : this.downloadsString;
  }

  public int getReleases() {
    return this.releases;
  }

  public int getId() {
    return this.id;
  }

  public int getVersion() {
    return this.version;
  }

  public int getDownloads() {
    return this.downloads;
  }

  public boolean isFeatured() {
    return this.featured;
  }

  public long getLastUpdate() {
    return this.lastUpdate * 1000L;
  }

  public Optional<List<Review>> getOrLoadReviews(ResultCallback<List<Review>> consumer) {
    return CONTROLLER.getOrLoadReviews(this.namespace, consumer);
  }

  public Optional<List<Changelog>> getOrLoadChangelog(ResultCallback<List<Changelog>> consumer) {
    return CONTROLLER.getOrLoadChangelog(this.namespace, consumer);
  }

  public MarkdownDocument getOrLoadDescription(ResultCallback<MarkdownDocument> consumer) {
    if(this.description == null) {
      CONTROLLER.loadDescription(this.namespace, (result) -> {
        if(result.hasException()) {
          consumer.acceptException(result.exception());
        } else if(result.isEmpty()) {
          consumer.acceptRaw(null);
        } else {
          this.description = Laby.references().markdownParser().parse(result.get());
          consumer.acceptRaw(this.description);
        }
      });
    }

    return this.description;
  }

  public Set<FinderTag> getTags() {
    if(this.finderTags == null) {
      Set<FinderTag> finderTags = Sets.newHashSet();

      if(this.tags != null) {
        for(int tag : this.tags) {
          Optional<FinderTag> optionalFinderTag = CONTROLLER.getTag(tag);

          Objects.requireNonNull(finderTags);
          optionalFinderTag.ifPresent(finderTags::add);
        }
      }

      this.finderTags = finderTags;
    }

    return this.finderTags;
  }

  public Image getIcon() {
    if(this.icon == null && this.brandImages != null) {
      for(Image brandImage : this.brandImages) {
        if(brandImage.getType() == ImageType.ICON) {
          this.icon = brandImage;
          break;
        }
      }
    }

    return this.icon;
  }

  public Image getThumbnail() {
    if(this.thumbnail == null && this.brandImages != null) {
      for(Image brandImage : this.brandImages) {
        if(brandImage.getType() == ImageType.THUMBNAIL) {
          this.thumbnail = brandImage;
          break;
        }
      }
    }

    return this.thumbnail;
  }

  public Image[] getSliderImages() {
    if(this.sliderImages == null && this.brandImages != null) {
      List<Image> sliderImages = Lists.newArrayList();

      for(Image brandImage : this.brandImages) {
        if(brandImage.getType() == ImageType.IMAGE) {
          sliderImages.add(brandImage);
        }
      }

      this.sliderImages = sliderImages.toArray(new Image[0]);
    }

    return this.sliderImages;
  }

  public Image[] getBrandImages() {
    return this.brandImages;
  }

  public String getAuthor() {
    if(this.author == null) {
      this.author = "Epyc Solutions";
      return this.author;
    }

    return this.author;
  }

  public FinderEnviron loadEnviron(ResultCallback<FinderEnviron> result) {
    return CONTROLLER.loadEnviron(this.namespace, result);
  }

  public Optional<LoadedEnviron> getAsLoadedEnviron() {
    return DefaultEnvironService.getInstance().getEnviron(this.namespace);
  }

  public boolean isInstalled() {
    return getAsLoadedEnviron().isPresent();
  }

  public String[] getRawPermissions() {
    return this.permissions;
  }

  public FinderPermission[] getPermissions() {
    if(this.finderPermissions == null) {
      ArrayList<FinderPermission> permissions = new ArrayList<>(this.permissions.length);
      for(String key : this.permissions) {
        permissions.add(CONTROLLER.getPermission(key));
      }

      permissions.sort(Comparator.comparing((permission) -> !permission.isCritical()));
      this.finderPermissions = permissions.toArray(new FinderPermission[0]);
    }

    return this.finderPermissions;
  }



  public int getRanking() {
    return this.ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public boolean hasMeta(EnvironMeta environMeta) {
    if(this.meta == null) return false;

    for(EnvironMeta meta : this.meta) {
      if(meta == environMeta) {
        return true;
      }
    }

    return false;
  }

  public enum ImageType {
    ICON(256),
    THUMBNAIL(1280),
    IMAGE(1280, 1920);

    private final int smallSize;
    private final int fullSize;

    ImageType(int size) {
      this.smallSize = size;
      this.fullSize = size;
    }

    ImageType(int smallSize, int fullSize) {
      this.smallSize = smallSize;
      this.fullSize = fullSize;
    }

    public int getSmallSize() {
      return this.smallSize;
    }

    public int getFullSize() {
      return this.fullSize;
    }

    public static ImageType of(String name) {
      for(ImageType imageType : values()) {
        if(imageType.name().equalsIgnoreCase(name)) {
          return imageType;
        }
      }

      return null;
    }
  }

  public static class Rating {
    private final int count;
    private final double rating;

    public Rating(int count, double rating) {
      this.count = count;
      this.rating = rating;
    }

    public int getCount() {
      return this.count;
    }

    public double getRating() {
      return this.rating;
    }
  }

  public static class Image {
    private String type;
    private String hash;

    public ImageType getType() {
      return ImageType.of(this.type);
    }

    public String getIconUrl() {
      return String.format(Urls.ENVIRON_SERVICE_BACKEND + "/icons/%s.png", this.hash);
    }
  }

  public static class Review {
    private int rating;

    private String comment;
    private AccessoriesUser user;

    @SerializedName("added_at")
    private String addedAt;
    private long addedAtLong = -1L;

    public int getRating() {
      return this.rating;
    }

    public String getComment() {
      return this.comment;
    }

    public AccessoriesUser user() {
      return this.user;
    }

    public String getAddedAtString() {
      return this.addedAt;
    }

    public long getAddedAt() {
      if(this.addedAtLong == -1L) {
        try {
          this.addedAtLong = DATE_FORMAT.parse(this.addedAt).getTime();
        } catch(ParseException exception) {
          exception.printStackTrace();
        }
      }

      return this.addedAtLong;
    }
  }

  public static class Changelog {
    private String changelog;
    private String release;

    @SerializedName("added_at")
    private String addedAt;
    private long addedAtLong;

    private VersionCompatibility releaseVersion;

    public Changelog() {
      this.addedAtLong = -1L;
    }

    public String getChanges() {
      return this.changelog;
    }

    public String getRelease() {
      return this.release;
    }

    public VersionCompatibility releaseVersion() {
      if(this.releaseVersion == null) {
        this.releaseVersion = new SemanticVersion(this.release);
      }

      return this.releaseVersion;
    }

    public String getAddedAtString() {
      return this.addedAt;
    }

    public long getAddedAt() {
      if(this.addedAtLong == -1L) {
        try {
          this.addedAtLong = DATE_FORMAT.parse(this.addedAt).getTime();
        } catch(ParseException exception) {
          exception.printStackTrace();
        }
      }

      return this.addedAtLong;
    }
  }

}
