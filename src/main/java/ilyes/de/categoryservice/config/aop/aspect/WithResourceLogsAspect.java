package ilyes.de.categoryservice.config.aop.aspect;

import ilyes.de.categoryservice.config.aop.annotation.WithResourceLogs;
import ilyes.de.categoryservice.config.exception.TechnicalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ilyes.de.categoryservice.config.log.dto.LogContentDTOFactory.createLogContentDTOAsJsonStringWithDataAndLogType;
import static ilyes.de.categoryservice.config.log.logtype.LogTypeConstants.CATEGORY_TECHNICAL_ERROR;

@Aspect
@Component
public class WithResourceLogsAspect {
    private static final Logger LOGGER = LogManager.getLogger(WithResourceLogsAspect.class);

    @Around(value = "@annotation(withResourceLogs)")
    public Object around(ProceedingJoinPoint joinPoint, WithResourceLogs withResourceLogs) throws Throwable {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = ra.getRequest();
        HttpServletResponse httpServletResponse = ra.getResponse();
        String logType = withResourceLogs.logType();
        String logTypeRequest = String.format("%s_BEGIN", logType);
        String logTypeResponse = String.format("%s_END", logType);
        String logTypeWarn = String.format("%s_WARN", logType);
        String logTypeError = String.format("%s_ERROR", logType);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] sigParamNames = methodSignature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        Map<String, Object> requestVariables = new HashMap<>();
        for (int i = 0; i < paramValues.length; i++) {
            String paramName = sigParamNames[i];
            if (!paramName.equals("httpServletRequest")) {
                requestVariables.put(paramName, paramValues[i]);
            }
        }

        Map<String, Object> requestLogContent = new HashMap<>();
        requestLogContent.put("requestVariables", requestVariables);
        requestLogContent.put("pathUrl", httpServletRequest.getRequestURI());
        requestLogContent.put("httpMethod", httpServletRequest.getMethod());
        requestLogContent.put("clientIp", httpServletRequest.getRemoteAddr());

        if (withResourceLogs.shouldLogHeaders()) {
            Map<String, Serializable> headers = Collections.list(httpServletRequest.getHeaderNames()).stream().collect(Collectors.toMap(h -> h, h -> {
                ArrayList<String> headerValues = Collections.list(httpServletRequest.getHeaders(h));
                return headerValues.size() == 1 ? headerValues.get(0) : headerValues;
            }));
            requestLogContent.put("headers", headers);
        }

        if (withResourceLogs.shouldLogQueryParams()) {
            requestLogContent.put("queryParams", httpServletRequest.getParameterMap());
        }

        Map<String, Object> logData = Map.of("request", requestLogContent);

        LOGGER.info(createLogContentDTOAsJsonStringWithDataAndLogType(logData, logTypeRequest));
        try {
            Object responseBody = joinPoint.proceed();
            stopWatch.stop();

            Map<String, Object> logResponse = Map.of("response",
                    Map.of(
                            "body",
                            responseBody,
                            "clientIp", httpServletRequest.getRemoteAddr(),
                            "pathUrl", httpServletRequest.getRequestURI(),
                            "responseTimeNano", stopWatch.getTotalTimeNanos(),
                            "responseTime", stopWatch.getTotalTimeSeconds(),
                            "httpStatus",
                            httpServletResponse.getStatus(),
                            "httpMethod",
                            httpServletRequest.getMethod()
                    )
            );
            LOGGER.info(createLogContentDTOAsJsonStringWithDataAndLogType(logResponse, logTypeResponse));
            return responseBody;
        } catch (TechnicalException ex) {
            if (ex.getLogType().equals(CATEGORY_TECHNICAL_ERROR)) {
                if (ex.getErrorHttpStatus().is4xxClientError()) {
                    ex.setLogType(logTypeWarn);
                } else {
                    ex.setLogType(logTypeError);
                }
            }
            throw ex;
        } catch (NullPointerException ex) {
            throw new TechnicalException("internal server error!", ex, logTypeError, Map.of("exceptionType", "null pointer exception!"));
        } catch (RuntimeException ex) {
            throw new TechnicalException("internal server error!", ex, logTypeError, Map.of("exceptionType", ex.getClass(), "errorMessage", ex.getMessage()));
        } finally {
            ThreadContext.clearAll();
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }
    }
}
