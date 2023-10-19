package eu.epycsolutions.labyaddon.playeraccessories.environ.finder;

public class FinderPermission {

  private final int id;
  private final String key;
  private final boolean critical;

  public FinderPermission(String key) {
    this.key = key;
    this.id = -1;
    this.critical = true;
  }

  public int getId() {
    return this.id;
  }

  public String getKey() {
    return this.key;
  }

  public boolean isCritical() {
    return this.critical;
  }

}
