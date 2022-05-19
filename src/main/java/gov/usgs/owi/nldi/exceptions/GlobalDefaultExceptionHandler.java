package gov.usgs.owi.nldi.exceptions;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

  @ExceptionHandler({
    FeatureSourceNotFoundException.class,
    FeatureIdNotFoundException.class,
    DataSourceNotFoundException.class
  })
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  public String handleNotFoundException(Exception exception) {
    return exception.getLocalizedMessage();
  }

  @ExceptionHandler({ConstraintViolationException.class, ResponseStatusException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleBadRequestException(Exception exception) {
    return exception.getLocalizedMessage();
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String errorMessage;
    String currentMessage = exception.getLocalizedMessage();

    if (currentMessage.contains("\n")) {
      // This exception's message contains implementation details after the new line,
      // so only take up to that.
      errorMessage = currentMessage.substring(0, exception.getLocalizedMessage().indexOf("\n"));
    } else {
      errorMessage = currentMessage.replaceAll("([a-zA-Z]+\\.)+", "");
    }

    Object entityBody = errorMessage;
    return new ResponseEntity(entityBody, headers, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return new ResponseEntity<>(ex.getLocalizedMessage(), headers, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleUncaughtException(Exception exception, HttpServletResponse response) {
    int hashValue = response.hashCode();
    // Note: we are giving the user a generic message.
    // Server logs can be used to troubleshoot problems.
    String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
    LOG.error(msgText, exception);
    return msgText;
  }
}
