package gr.uoa.di.error;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class ResponseProcessingException extends RuntimeException {

  public ResponseProcessingException(String message) {
    super(message);
  }

  public ResponseProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
