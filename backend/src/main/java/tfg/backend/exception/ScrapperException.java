package tfg.backend.exception;

public class ScrapperException extends BackendRuntimeException {

  // Default constructors without args
  public ScrapperException() {
    super();
  }

  public ScrapperException(String message) {
    super(message);
  }

  public ScrapperException(Throwable cause) {
    super(cause);
  }

  public ScrapperException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructors with args
  public ScrapperException(String[] args) {
    super(args);
  }

  public ScrapperException(String message, String[] args) {
    super(message, args);
  }

  public ScrapperException(String[] args, Throwable cause) {
    super(args, cause);
  }

  public ScrapperException(String message, String[] args, Throwable cause) {
    super(message, args, cause);
  }

  public ScrapperException(String message, Throwable cause, String[] args) {
    super(message, cause, args);
  }

}