package tfg.backend.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tfg.backend.exception.DuplicateSerieException;
import tfg.backend.exception.ScrapperException;
import tfg.backend.exception.SerieNotFoundException;
import tfg.backend.utils.ExceptionUtils;

@Slf4j
@ControllerAdvice
public class ControllerAdviser extends ResponseEntityExceptionHandler {

  @Autowired
  private MessageSource messageSource;
  @Autowired
  Environment env;

  /**
   * We are expecting to have an exception and handle it.
   */
  @ExceptionHandler({ Exception.class })
  public ResponseEntity<ExceptionResponse> handleRestException(Exception ex, WebRequest request) {
    return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
  }

  /**
   * Generic exception for validation errors.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException  ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return new ResponseEntity<>(buildExceptionResponse(HttpStatus.BAD_REQUEST, ex).getBody(),
        status);
  }

  @ExceptionHandler({ SerieNotFoundException.class})
  public ResponseEntity<ExceptionResponse> handleCategoriaNotFoundException(
          SerieNotFoundException ex, WebRequest request) {
    return buildExceptionResponse(HttpStatus.NOT_FOUND, ex);
  }

  @ExceptionHandler({ ScrapperException.class})
  public ResponseEntity<ExceptionResponse> handleScrapperException(
          ScrapperException ex, WebRequest request) {
    return buildExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, ex);
  }

  @ExceptionHandler({DuplicateSerieException.class})
  public ResponseEntity<ExceptionResponse> handleDuplicateSerieException(DuplicateSerieException ex, WebRequest request)
  {
    return  buildExceptionResponse(HttpStatus.CONFLICT, ex);
  }

  /**
   * Exception throw handled when the requested operation is not supported.
   *
   * @param ex      Handled Exception
   * @param request Http request
   * @return Http response
   */
  @ExceptionHandler({ UnsupportedOperationException.class })
  public ResponseEntity<ExceptionResponse> handleUnsupportedOperationException(
      UnsupportedOperationException ex, WebRequest request) {
    return buildExceptionResponse(HttpStatus.METHOD_NOT_ALLOWED, ex);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      @NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
      @NonNull HttpStatus status, @NonNull WebRequest request) {
    return handleInternalException(ex, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      @NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers,
      @NonNull HttpStatus status, @NonNull WebRequest request) {
    if (ex.getCause() instanceof InvalidFormatException) {
      InvalidFormatException exception = (InvalidFormatException) ex.getCause();
      return new ResponseEntity<>(buildExceptionResponse(HttpStatus.BAD_REQUEST, exception)
          .getBody(), status);
    }
    return handleInternalException(ex, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      @NonNull HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers,
      @NonNull HttpStatus status, @NonNull WebRequest request) {
    return handleInternalException(ex, headers, status, request);
  }

  private ResponseEntity<Object> handleInternalException(Exception ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    ResponseEntity<ExceptionResponse> exceptionResponse =
        buildExceptionResponse(HttpStatus.BAD_REQUEST, ex);
    return new ResponseEntity<>(exceptionResponse.getBody(), exceptionResponse.getStatusCode());
  }

  private ResponseEntity<ExceptionResponse> buildExceptionResponse(HttpStatus status,
                                                                   Exception ex) {
    ExceptionResponse result;

    //If it is a validation exception, process the message errors
    if (ex instanceof MethodArgumentNotValidException) {
      result = this.processSpringValidationErrorResponse(status, ex);
    } else if (ex instanceof InvalidFormatException) {
      result = processInvalidFormatException(status, (InvalidFormatException) ex);
    } else {
      result = new ExceptionResponse(status, ex);
    }

    ExceptionUtils.log(ex);
    return new ResponseEntity<>(result, status);
  }

  private ExceptionResponse processInvalidFormatException(HttpStatus status,
      InvalidFormatException ex) {
    ExceptionResponse result = new ExceptionResponse(status, ex);
    if (Pattern.compile("Enum class[^\"\']").matcher(ex.getLocalizedMessage()).find()) {
      String values = ex.getLocalizedMessage().substring(ex.getLocalizedMessage().indexOf('[') + 1,
          ex.getLocalizedMessage().indexOf(']'));
      result.setMessage(ExceptionUtils.processMessage("invalid.enum.error",
          new String[] { ex.getValue().toString(), values }));
    }
    return result;
  }

  private ExceptionResponse processSpringValidationErrorResponse(HttpStatus status,
      Exception ex) {
    ExceptionResponse result = new ExceptionResponse(status, ex);

    StringBuilder message = new StringBuilder();
    ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors()
        .forEach(err -> message
            .append(ExceptionUtils.processMessage(err.getDefaultMessage()))
            .append(System.lineSeparator()));

    result.setMessage(message.toString());

    return result;
  }

  @Getter
  @Setter
  protected class ExceptionResponse {
    private int errorCode;
    private String errorDescription;
    private String message;

    public ExceptionResponse(HttpStatus status, Exception exception) {
      this.errorCode = status.value();
      this.errorDescription = status.getReasonPhrase();
      this.message = exception.getMessage();
    }

  }

}
