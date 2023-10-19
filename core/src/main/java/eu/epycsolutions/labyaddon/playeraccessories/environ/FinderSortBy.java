package eu.epycsolutions.labyaddon.playeraccessories.environ;

import java.util.Locale;

public enum FinderSortBy {

  TRENDING,
  NAME_AZ,
  NAME_ZA,
  DOWNLOADS,
  LATEST,
  OLDEST,
  RATING;


  public String toString() {
    return name().toUpperCase(Locale.ENGLISH);
  }

}
