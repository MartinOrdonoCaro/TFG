package tfg.backend.exception;

public class SerieNotFoundException extends BackendRuntimeException {

  // Default constructors without args
  public SerieNotFoundException() {
    super();
  }

  public SerieNotFoundException(String message) {
    super(message);
  }

  public SerieNotFoundException(Throwable cause) {
    super(cause);
  }

  public SerieNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructors with args
  public SerieNotFoundException(String[] args) {
    super(args);
  }

  public SerieNotFoundException(String message, String[] args) {
    super(message, args);
  }

  public SerieNotFoundException(String[] args, Throwable cause) {
    super(args, cause);
  }

  public SerieNotFoundException(String message, String[] args, Throwable cause) {
    super(message, args, cause);
  }

  public SerieNotFoundException(String message, Throwable cause, String[] args) {
    super(message, cause, args);
  }

}