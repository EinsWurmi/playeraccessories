package eu.epycsolutions.labyaddon.playeraccessories.model.environ.info;

public class InstalledEnvironInfo {

  private String namespace;

  private EnvironMeta[] meta;

  private String displayName;
  private String description;
  private String version;
  private String author;
  private String mainClass;

  public String getNamespace() {
    return namespace;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }

  public String getAuthor() {
    return author;
  }

  public EnvironMeta[] meta() {
    return meta;
  }

  public boolean hasMeta(EnvironMeta meta) {
    for(EnvironMeta presentMeta : this.meta) {
      if(presentMeta == meta) return true;
    }

    return false;
  }

  public String getMainClass() {
    return mainClass;
  }

}
