package eu.epycsolutions.labyaddon.playeraccessories.builddata;

import net.labymod.api.models.version.Version;

public class BuildDataModel {

  private Version latestLabyFullRelease;
  private Version labyVersion;

  private int buildNumber;

  private Version latestAccessoriesFullRelease;
  private Version accessoriesVersion;

  private String releaseType;
  private String commitReference;
  private String branchName;

  private String chatTrustFeature;

  public Version latestLabyFullRelease() {
    return this.latestLabyFullRelease;
  }

  public Version labyVersion() {
    return this.labyVersion;
  }

  public Version latestAccessoriesFullRelease() {
    return this.latestAccessoriesFullRelease;
  }

  public Version accessoriesVersion() {
    return this.accessoriesVersion;
  }

  public String getReleaseType() {
    return this.releaseType;
  }

  public String getCommitReference() {
    return this.commitReference;
  }

  public String getBranchName() {
    return this.branchName;
  }

  public String getChatTrustFeature() {
    return this.chatTrustFeature;
  }

  public int getBuildNumber() {
    return this.buildNumber;
  }

}
