package ilyes.de.categoryservice.config.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonPropertyOrder({"error","messages","statusCode","requestId"})
public class ErrorResponseTo {
    @JsonProperty("error")
    private String errorSummary;
    @JsonProperty("messages")
    private List<String> errorMessages;
    @JsonIgnore
    private HttpStatus errorHttpStatus;

    private String requestId;

    public ErrorResponseTo(String errorSummary, List<String> errorMessages, HttpStatus errorHttpStatus) {
        this.errorSummary = errorSummary;
        this.errorMessages = errorMessages;
        this.errorHttpStatus = errorHttpStatus;
    }

    public ErrorResponseTo(String errorSummary, List<String> errorMessages, HttpStatus errorHttpStatus, String requestId) {
        this.errorSummary = errorSummary;
        this.errorMessages = errorMessages;
        this.errorHttpStatus = errorHttpStatus;
        this.requestId = requestId;
    }

    public ErrorResponseTo(String errorSummary, HttpStatus errorHttpStatus) {
        this.errorSummary = errorSummary;
        this.errorHttpStatus = errorHttpStatus;
    }

    public ErrorResponseTo() {
    }

    public String getErrorSummary() {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public HttpStatus getErrorHttpStatus() {
        return errorHttpStatus;
    }

    @JsonProperty("statusCode")
    public int getHttpStatusCodeAsInt() {
        return this.errorHttpStatus.value();
    }

    public void setErrorHttpStatus(HttpStatus errorHttpStatus) {
        this.errorHttpStatus = errorHttpStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ErrorResponseTo{" +
                "errorSummary='" + errorSummary + '\'' +
                ", errorMessages=" + errorMessages +
                ", errorHttpStatus=" + errorHttpStatus +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
