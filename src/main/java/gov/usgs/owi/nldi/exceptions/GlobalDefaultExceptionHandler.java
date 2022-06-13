package gov.usgs.owi.nldi.exceptions;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

  @ExceptionHandler({
    FeatureSourceNotFoundException.class,
    FeatureIdNotFoundException.class,
    DataSourceNotFoundException.class
  })
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  public Error handleNotFoundException(Exception exception) {
    return new Error(exception.getLocalizedMessage());
  }

  @ExceptionHandler({
          ConstraintViolationException.class,
          ResponseStatusException.class,
          MissingServletRequestParameterException.class
  })
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Error handleBadRequestException(Exception exception) {
    return new Error(exception.getLocalizedMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Error handleMethodArgumentTypeMistmatchException(MethodArgumentTypeMismatchException exception) {
    // simplifies the message and avoids listing the nested exceptions
    String finalMessage = exception.getMessage().split("IllegalArgumentException: ")[1];
    return new Error(finalMessage);
  }

  @ExceptionHandler({
          HttpMessageNotReadableException.class
  })
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Error handleBadRequestTrimmedException(Exception exception) {
    String errorMessage;
    String currentMessage = exception.getLocalizedMessage();

    if (currentMessage.contains("\n")) {
      // This exception's message contains implementation details after the new line,
      // so only take up to that.
      errorMessage = currentMessage.substring(0, exception.getLocalizedMessage().indexOf("\n"));
    } else {
      errorMessage = currentMessage.replaceAll("([a-zA-Z]+\\.)+", "");
    }

    return new Error(errorMessage);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
  @ResponseBody
  public Error handleHttpMediaTypeNotAcceptableExeption(HttpMediaTypeNotAcceptableException exception) {
    return new Error(exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Error handleUncaughtException(Exception exception, HttpServletResponse response) {
    int hashValue = response.hashCode();
    // Note: we are giving the user a generic message.
    // Server logs can be used to troubleshoot problems.
    String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
    LOG.error(msgText, exception);
    return new Error(msgText);
  }
}
