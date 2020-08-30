package tfg.backend.exception;

import lombok.Getter;
import tfg.backend.utils.ExceptionUtils;

@Getter
public class BackendRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 4582447316194662740L;

  private final String[] args;

  public boolean isPrintStacktrace() {
    return false;
  }

  // Default constructors without args
  public BackendRuntimeException() {
    super();
    this.args = new String[]{};
  }

  public BackendRuntimeException(String message) {
    super(ExceptionUtils.processMessage(message));
    this.args = new String[]{};
  }

  public BackendRuntimeException(Throwable cause) {
    super(cause);
    this.args = new String[]{};
  }

  public BackendRuntimeException(String message, Throwable cause) {
    super(ExceptionUtils.processMessage(message), cause);
    this.args = new String[]{};
  }

  // Constructors with args
  public BackendRuntimeException(String[] args) {
    super();
    this.args = args;
  }

  public BackendRuntimeException(String message, String[] args) {
    super(ExceptionUtils.processMessage(message, args));
    this.args = args;
  }

  public BackendRuntimeException(String[] args, Throwable cause) {
    super(cause);
    this.args = args;
  }

  public BackendRuntimeException(String message, String[] args, Throwable cause) {
    super(ExceptionUtils.processMessage(message, args), cause);
    this.args = args;
  }

  public BackendRuntimeException(String message, Throwable cause, String[] args) {
    super(ExceptionUtils.processMessage(message, args), cause);
    this.args = args;
  }

}