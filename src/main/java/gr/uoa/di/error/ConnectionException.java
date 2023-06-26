package gr.uoa.di.error;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class ConnectionException extends RuntimeException {

  public ConnectionException(String message) {
    super(message);
  }

  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

}