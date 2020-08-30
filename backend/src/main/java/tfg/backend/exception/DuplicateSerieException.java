package tfg.backend.exception;

public class DuplicateSerieException extends BackendRuntimeException {

  // Default constructors without args
  public DuplicateSerieException() {
    super();
  }

  public DuplicateSerieException(String message) {
    super(message);
  }

  public DuplicateSerieException(Throwable cause) {
    super(cause);
  }

  public DuplicateSerieException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructors with args
  public DuplicateSerieException(String[] args) {
    super(args);
  }

  public DuplicateSerieException(String message, String[] args) {
    super(message, args);
  }

  public DuplicateSerieException(String[] args, Throwable cause) {
    super(args, cause);
  }

  public DuplicateSerieException(String message, String[] args, Throwable cause) {
    super(message, args, cause);
  }

  public DuplicateSerieException(String message, Throwable cause, String[] args) {
    super(message, cause, args);
  }

}