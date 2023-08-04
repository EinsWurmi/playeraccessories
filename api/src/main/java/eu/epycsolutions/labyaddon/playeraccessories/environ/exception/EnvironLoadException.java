package eu.epycsolutions.labyaddon.playeraccessories.environ.exception;

public class EnvironLoadException extends RuntimeException {

  public EnvironLoadException() { }

  public EnvironLoadException(String message) {
    super(message);
  }

  public EnvironLoadException(String message, Throwable cause) {
    super(message, cause);
  }

}
