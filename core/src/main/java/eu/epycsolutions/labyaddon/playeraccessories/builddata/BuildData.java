package eu.epycsolutions.labyaddon.playeraccessories.builddata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.labymod.api.loader.LabyModLoader;
import net.labymod.api.loader.platform.PlatformEnvironment;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.version.SemanticVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class BuildData {

  private static final String PRODUCTION_NAME = "production";
  private static String qualifiedVersion = null;

  private static BuildDataModel model;

  private static BuildDataModel model() {
    if(model != null) return model;

    Gson gson = (new GsonBuilder()).registerTypeAdapter(Version.class, new SchematicVersionTypeAdapterFactory()).create();
    ClassLoader classLoader = PlatformEnvironment.getPlatformClassloader().getPlatformClassloader();

    try {
      InputStream stream = classLoader.getResourceAsStream("build-data.json");

      try {
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream));
        try {
          model = gson.fromJson(reader, BuildDataModel.class);

          BuildDataModel buildDataModel = model;
          reader.close();

          if(stream != null) stream.close();

          return buildDataModel;
        } catch(Throwable throwable) {
          try {
            reader.close();
          } catch(Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }

          throw throwable;
        }
      } catch(Throwable throwable) {
        if(stream != null) {
          try {
            stream.close();
          } catch(Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }

          throw throwable;
        }
      }
    } catch(IOException exception) {
      throw new RuntimeException("Could not read build-data.json");
    }

    return null;
  }

  private static String qualifiedVersion() {
    BuildDataModel model = model();

    String releaseType = model.getReleaseType();
    String branchName = model.getBranchName();
    String commitReference = model.getCommitReference();

    if(!branchName.equals(releaseType)) {
      qualifiedVersion = String.format("%s+%s %s/%s",
              model.labyVersion().toString(),
              releaseType,
              branchName,
              commitReference
      );
    } else if(qualifiedVersion == null) {
      if(PRODUCTION_NAME.equals(releaseType)) {
        qualifiedVersion = model.labyVersion().toString();
      } else {
        qualifiedVersion = String.format("%s+%s %s/%s",
            model.labyVersion().toString(),
            model.getBuildNumber(),
            releaseType,
            commitReference
        );
      }
    }

    return qualifiedVersion;
  }

  public static String commitReference() {
    return model().getCommitReference();
  }

  public static String branchName() {
    return model().getBranchName();
  }

  public static String releaseType() {
    return model().getReleaseType();
  }

  public static Version version() {
    return model.labyVersion();
  }

  public static Version latestFullRelease() {
    return model().latestLabyFullRelease();
  }

  public static int getBuildNumber() {
    return model().getBuildNumber();
  }

  private static class SchematicVersionTypeAdapterFactory extends TypeAdapter<Version> {
    public void write(JsonWriter out, Version value) throws IOException {
      out.value(value.toString());
    }

    public Version read(JsonReader in) throws IOException {
      return new SemanticVersion(in.nextString());
    }
  }

}
