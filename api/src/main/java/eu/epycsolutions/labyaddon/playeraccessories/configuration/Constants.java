package eu.epycsolutions.labyaddon.playeraccessories.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {

  public static class AccessoriesFiles {
    public static final Path LABYMOD_DIRECTORY = net.labymod.api.Constants.Files.LABYMOD_DIRECTORY;
    public static final Path LABYMOD_ASSETS = net.labymod.api.Constants.Files.LABYMOD_ASSETS;

    public static final Path PLAYERACCESSORIES_ADDON_CONFIG = LABYMOD_DIRECTORY.resolve("configs/player-accessories");

    public static final Path ENVIRON_MILIEUS = PLAYERACCESSORIES_ADDON_CONFIG.resolve("milieus");
  }

  @Deprecated
  public static class Files {

    public static final Path LABYMOD_DIR = Paths.get("labymod-neo");

    public static final Path PLAYERACCESSORIES_ADDON_CONFIG = LABYMOD_DIR.resolve("configs/player-accessories");

    public static final Path SERVER_SUPPORT_CONFIGS = PLAYERACCESSORIES_ADDON_CONFIG.resolve("server-support");

    public static final Path GG_SERVER_SUPPORT = SERVER_SUPPORT_CONFIGS.resolve("griefergames");

    public static final Path GG_SCAMMER_LIST = GG_SERVER_SUPPORT.resolve("scammers.json");

    public static final Path GG_MIDDLEMAN_LIST = GG_SERVER_SUPPORT.resolve("middleman.json");

  }

  public static class Urls {
    public static final String LABYMOD_WEB_BASE = net.labymod.api.Constants.Urls.WEB_BASE;
    public static final String LABYNET_WEB_BASE = net.labymod.api.Constants.Urls.LABYNET_BASE;

    public static final String LABYNET_SKIN_BASE = net.labymod.api.Constants.Urls.SKIN_LABYNET_BASE;
    public static final String LABYNET_TEXTURE_BASE = net.labymod.api.Constants.Urls.TEXTURE_LABYNET_BASE;
  }

  @Deprecated
  public static class LegacyUrls {

    public static final String LABYNET_PROFILE_NAME = labyNetProtocol("@%s");

    public static final String GG_SCAMMERS = ggProtocol("scammer/scammers");

    public static final String GG_MIDDLEMANS = ggProtocol("mm/middlemans");

    private static String labyNetProtocol(String path) {
      return "https://laby.net/" + path;
    }

    private static String ggProtocol(String path) {
      return "http://newh1ve.de:8080/" + path;
    }

  }

}
