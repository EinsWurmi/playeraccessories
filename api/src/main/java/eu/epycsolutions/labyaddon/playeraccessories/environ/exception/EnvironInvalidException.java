package eu.epycsolutions.labyaddon.playeraccessories.environ.exception;

public class EnvironInvalidException extends IllegalStateException {

  public EnvironInvalidException() { }

  public EnvironInvalidException(String message) {
    super(message);
  }

  public EnvironInvalidException(String message, Throwable cause) {
    super(message, cause);
  }

}
