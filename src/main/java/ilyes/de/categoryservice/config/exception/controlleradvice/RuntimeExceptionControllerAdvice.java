package ilyes.de.categoryservice.config.exception.controlleradvice;

import ilyes.de.categoryservice.config.exception.ErrorResponseTo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static ilyes.de.categoryservice.config.log.dto.LogContentDTOFactory.createLogContentDTOAsJsonString;
import static ilyes.de.categoryservice.config.log.logtype.LogTypeConstants.CATEGORY_RUNTIME_ERROR;

@ControllerAdvice
public class RuntimeExceptionControllerAdvice {
    private static final Logger LOGGER = LogManager.getLogger(RuntimeExceptionControllerAdvice.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    ResponseEntity<ErrorResponseTo> onRuntimeException(
            HttpServletRequest httpServletRequest,
            RuntimeException e) {
        String errorSummary = "Internal Server error!";

        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
                errorSummary,
                List.of(errorSummary),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ThreadContext.get("requestId")
        );
        var logContent = Map.of("response", Map.of(
                "body",
                errorResponseTo,
                "pathUrl",
                httpServletRequest.getRequestURI(),
                "httpMethod",
                httpServletRequest.getMethod()
        ));
        LOGGER.error(createLogContentDTOAsJsonString(logContent, CATEGORY_RUNTIME_ERROR,errorSummary),e);

        return new ResponseEntity<>(
                errorResponseTo,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
