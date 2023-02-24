package ilyes.de.categoryservice.config.log.logeventfactory;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ilyes.de.categoryservice.config.exception.TechnicalException;
import ilyes.de.categoryservice.config.log.dto.LogContentDTO;
import ilyes.de.categoryservice.config.utils.objectmapper.ObjectMapperUtilsFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ilyes.de.categoryservice.config.log.dto.LogContentDTOFactory.createLogContentDTOAsJsonStringWithTitle;

public class MaskSensitiveDataLogEventFactory implements LogEventFactory {
    private final List<String> SENSITIVE_KEYS_TO_MASK = List.of(
            "email",
            "sensitive",
            "hhh"
    );

    private final List<String> SENSITIVE_KEYS_TO_IGNORE_MASK = List.of(
            ".data.response.body.leader.email",
            // ".data.response.body.employees.email",
            ".sensitive"
    );

    @Override
    public LogEvent createEvent(String loggerName, Marker marker, String fqcn, Level level, Message message,
                                List<Property> properties, Throwable t) {

        if (loggerName.equals("org.springframework.web.servlet.PageNotFound")
                && message.getFormattedMessage().startsWith("No mapping for")) {
            throw new RuntimeException();
        }

        try {
            boolean isLogContentDTOMessage = message.getFormattedMessage().contains(LogContentDTO.CLASS_PROPERTY_NAME_LOG_TYPE)
                    && message.getFormattedMessage().contains(LogContentDTO.CLASS_PROPERTY_NAME_DATA);

            if (isLogContentDTOMessage) {
                ObjectMapper mapper = ObjectMapperUtilsFactory.createObjectMapperWithConcurrentMapByDefault();
                ConcurrentHashMap<String, Object> map = mapper.readValue(message.getFormattedMessage(), ConcurrentHashMap.class);
                try {
                    maskMapSensitiveData(map);
                    message = new ObjectMessage(map);
                } catch (RuntimeException ex) {
                    message = new ObjectMessage(map);
                }
            } else {
                ObjectMapper mapper = ObjectMapperUtilsFactory.createObjectMapperWithConcurrentMapByDefault();
                ConcurrentHashMap<String, Object> map = mapper.readValue(createLogContentDTOAsJsonStringWithTitle(message.getFormattedMessage()), ConcurrentHashMap.class);
                try {
                    maskMapSensitiveData(map);
                    message = new ObjectMessage(map);
                } catch (RuntimeException ex) {
                    message = new ObjectMessage(map);
                }
            }
        } catch (JsonProcessingException | TechnicalException ignored) {
            try {
                message = new ObjectMessage(createLogContentDTOAsJsonStringWithTitle(message.getFormattedMessage()));
            } catch (TechnicalException ex) {
                message = new StringFormattedMessage(String.format("Failed to serialize/deserialize json string, %s, %s", ex.getLogData().getOrDefault("exceptionMessage", ""), Arrays.toString(ex.getStackTrace())));
            }
        }

        return new Log4jLogEvent(loggerName, marker, fqcn, level, message, properties, t);
    }

    public void maskMapSensitiveData(ConcurrentHashMap<String, Object> rootMap) {
        this.maskMapSensitiveData(rootMap, "");
    }

    public void maskMapSensitiveData(ConcurrentHashMap<String, Object> rootMap, String parent) {

        rootMap.forEach((k, v) -> {
            if (v instanceof Map<?, ?>) {
                maskMapSensitiveData((ConcurrentHashMap<String, Object>) v, parent.concat("." + k));
            } else if (v instanceof List<?> && ((List<?>) v).get(0) instanceof Map<?,?>) {

                ((List<ConcurrentHashMap<String, Object>>) v).forEach(element -> maskMapSensitiveData(element, parent.concat("." + k)));
            } else {
                if (!SENSITIVE_KEYS_TO_IGNORE_MASK.contains(parent + "." + k) && SENSITIVE_KEYS_TO_MASK.contains(k)) {
                    if(v instanceof List<?>){
                        rootMap.put(k, ((List<?>) v).stream().map(e->"****").toList());
                    }else{
                        rootMap.put(k, "****");
                    }
                }
            }
        });

    }

}
