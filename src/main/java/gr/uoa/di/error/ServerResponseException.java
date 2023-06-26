package gr.uoa.di.error;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class ServerResponseException extends RuntimeException {

  private final int statusCode;

  public ServerResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
