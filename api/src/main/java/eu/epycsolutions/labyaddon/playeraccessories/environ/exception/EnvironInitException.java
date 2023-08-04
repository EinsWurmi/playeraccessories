package eu.epycsolutions.labyaddon.playeraccessories.environ.exception;

public class EnvironInitException extends RuntimeException {

  public EnvironInitException() { }

  public EnvironInitException(String message) {
    super(message);
  }

  public EnvironInitException(String message, Throwable cause) {
    super(message, cause);
  }

}
