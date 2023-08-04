package eu.epycsolutions.labyaddon.playeraccessories.model.environ.info;

import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.dependency.AddonDependency;
import eu.epycsolutions.labyaddon.playeraccessories.model.environ.info.dependency.EnvironDependency;
import net.labymod.api.models.OperatingSystem;
import net.labymod.api.models.version.VersionCompatibility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InstalledEnvironInfo {

  private String namespace;

  private String[] incompatibleAddons;
  private String[] incompatibleEnvirons;

  private OperatingSystem[] os;
  private VersionCompatibility compatibilityMinecraftVersions;

  private EnvironMeta[] meta;

  private AddonDependency[] addonDependencies;
  private EnvironDependency[] environDependencies;

  private String displayName;
  private String description;
  private String version;
  private String author;

  private String mainClass;
  private String fileName;

  private String[] earlyTransformers;
  private String[] transformers;

  private int requiredLabyModBuild;

  public String getNamespace() {
    return this.namespace;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public String getDescription() {
    return this.description;
  }

  public String getVersion() {
    return this.version;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getFileName() {
    return this.fileName;
  }

  public VersionCompatibility getCompatibilityMinecraftVersions() {
    return this.compatibilityMinecraftVersions;
  }

  public String[] getIncompatibleAddons() {
    return this.incompatibleAddons;
  }

  public String[] getIncompatibleEnvirons() {
    return this.incompatibleEnvirons;
  }

  public EnvironMeta[] meta() {
    return this.meta;
  }

  public boolean hasMeta(EnvironMeta meta) {
    for(EnvironMeta presentMeta : this.meta) {
      if(presentMeta == meta) return true;
    }

    return false;
  }

  public boolean isIncompatibleWithAddon(String addonId) {
    for(String incompatible : this.incompatibleAddons) {
      if(incompatible.equalsIgnoreCase(addonId)) return true;
    }

    return false;
  }

  public boolean isIncompatibleWithEnviron(String environId) {
    for(String incompatible : this.incompatibleEnvirons) {
      if(incompatible.equalsIgnoreCase(environId)) return true;
    }

    return false;
  }

  public boolean isCurrentOSSupported() {
    if(this.os == null || this.os.length == 0) return true;

    OperatingSystem platform = OperatingSystem.getPlatform();
    for(OperatingSystem os : this.os) {
      if(platform == os) return true;
    }

    return false;
  }

  public OperatingSystem[] getSupportedOperatingSystems() {
    return this.os;
  }

  public AddonDependency[] getAddonDependencies() {
    return this.addonDependencies;
  }

  public EnvironDependency[] getEnvironDependencies() {
    return this.environDependencies;
  }

  public String getMainClass() {
    return this.mainClass;
  }

  public int getRequiredLabyModBuild() {
    return this.requiredLabyModBuild;
  }

  public String[] getEarlyTransformers() {
    return this.earlyTransformers;
  }

  public String[] getTransformers() {
    return this.transformers;
  }

  public boolean hasAddonDependencies() {
    return (this.addonDependencies != null && this.addonDependencies.length != 0);
  }

  public boolean hasEnvironDependencies() {
    return (this.environDependencies != null && this.environDependencies.length != 0);
  }


  public InstalledEnvironInfo merge(InstalledEnvironInfo other) {
    InstalledEnvironInfo merged = new InstalledEnvironInfo();
    merged.namespace = getValue(this.namespace, other.namespace);

    merged.displayName = getValue(this.displayName, other.displayName);
    merged.description = getValue(this.description, other.description);
    merged.version = getValue(this.version, other.version);
    merged.author = getValue(this.author, other.author);

    merged.mainClass = getValue(this.mainClass, other.mainClass);

    merged.os = (this.os == null || this.os.length == 0) ? other.os : this.os;
    merged.compatibilityMinecraftVersions = this.compatibilityMinecraftVersions;
    merged.meta = this.meta;

    List<AddonDependency> addonDependencies = new ArrayList<>();
    addAll(addonDependencies, this.addonDependencies);
    addAll(addonDependencies, other.addonDependencies);
    merged.addonDependencies = addonDependencies.toArray(new AddonDependency[0]);

    List<EnvironDependency> environDependencies = new ArrayList<>();
    addAll(environDependencies, this.environDependencies);
    addAll(environDependencies, other.environDependencies);
    merged.environDependencies = environDependencies.toArray(new EnvironDependency[0]);

    List<String> incompatibleAddons = new ArrayList<>();
    addAll(incompatibleAddons, this.incompatibleAddons);
    addAll(incompatibleAddons, other.incompatibleAddons);
    merged.incompatibleAddons = incompatibleAddons.toArray(new String[0]);

    List<String> incompatibleEnvirons = new ArrayList<>();
    addAll(incompatibleEnvirons, this.incompatibleEnvirons);
    addAll(incompatibleEnvirons, other.incompatibleEnvirons);
    merged.incompatibleEnvirons = incompatibleEnvirons.toArray(new String[0]);

    List<String> earlyTransformers = new ArrayList<>();
    addAll(earlyTransformers, this.earlyTransformers);
    addAll(earlyTransformers, other.earlyTransformers);
    merged.earlyTransformers = earlyTransformers.toArray(new String[0]);

    List<String> transformers = new ArrayList<>();
    addAll(transformers, this.transformers);
    addAll(transformers, other.transformers);
    merged.transformers = transformers.toArray(new String[0]);

    return merged;
  }


  private String getValue(String value, String otherValue) {
    return (value == null || value.isEmpty()) ? otherValue : value;
  }

  private <T> void addAll(Collection<? super T> collection, T... elements) {
    if(elements == null) return;
    Collections.addAll(collection, elements);
  }

}
