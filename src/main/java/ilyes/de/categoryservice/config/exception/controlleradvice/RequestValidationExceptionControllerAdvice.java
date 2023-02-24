package ilyes.de.categoryservice.config.exception.controlleradvice;

import ilyes.de.categoryservice.config.exception.ErrorResponseTo;
import ilyes.de.categoryservice.config.log.dto.LogContentDTOFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Map;

import static ilyes.de.categoryservice.config.log.logtype.LogTypeConstants.CATEGORY_INPUT_VALIDATION_WARN;

@ControllerAdvice
public class RequestValidationExceptionControllerAdvice {
    private static final Logger LOGGER = LogManager.getLogger(RequestValidationExceptionControllerAdvice.class);
    private final String BAD_REQUEST_ERROR_SUMMARY = "Bad request, input validation failed!";
    private final String BAD_REQUEST_ERROR_MESSAGE_FORMAT = "Property '%s' validation failed. It %s";

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponseTo> onConstraintValidationException(
            HttpServletRequest httpServletRequest,
            ConstraintViolationException e) {
        ErrorResponseTo errorResponseTo = new ErrorResponseTo(BAD_REQUEST_ERROR_SUMMARY, new ArrayList<>(), HttpStatus.BAD_REQUEST);
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errorResponseTo.getErrorMessages().add(
                    String.format(BAD_REQUEST_ERROR_MESSAGE_FORMAT, violation.getPropertyPath().toString(), violation.getMessage())
            );
        }
        var logContent = Map.of("response", Map.of(
                "body",
                errorResponseTo,
                "pathUrl",
                httpServletRequest.getRequestURI(),
                "httpMethod",
                httpServletRequest.getMethod()
        ));
        LOGGER.warn(LogContentDTOFactory.createLogContentDTOAsJsonString(logContent, CATEGORY_INPUT_VALIDATION_WARN, errorResponseTo.getErrorSummary()), e);
        return new ResponseEntity<>(
                errorResponseTo,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseTo> onMethodArgumentNotValidException(
            HttpServletRequest httpServletRequest,
            MethodArgumentNotValidException e) {
        ErrorResponseTo errorResponseTo = new ErrorResponseTo(BAD_REQUEST_ERROR_SUMMARY, new ArrayList<>(), HttpStatus.BAD_REQUEST);
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorResponseTo.getErrorMessages().add(
                    String.format(BAD_REQUEST_ERROR_MESSAGE_FORMAT, fieldError.getField(), fieldError.getDefaultMessage())
            );
        }
        var logContent = Map.of("response", Map.of(
                "body",
                errorResponseTo,
                "pathUrl",
                httpServletRequest.getRequestURI(),
                "httpMethod",
                httpServletRequest.getMethod()
        ));
        LOGGER.warn(LogContentDTOFactory.createLogContentDTOAsJsonString(logContent, CATEGORY_INPUT_VALIDATION_WARN, errorResponseTo.getErrorSummary()), e);
        return new ResponseEntity<>(
                errorResponseTo,
                HttpStatus.BAD_REQUEST
        );
    }
}
