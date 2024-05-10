package account.util;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseBuilder {

    private Object status;
    private String error;
    private String message;
    private String path;

    public ResponseBuilder setStatus(Object status) {
        if (!(status instanceof String || status instanceof Integer)) {
            throw new IllegalArgumentException("Status must be a string or an integer");
        }
        this.status = status;
        return this;
    }

    public ResponseBuilder setError(String error) {
        this.error = error;
        return this;
    }

    public ResponseBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public ResponseBuilder badRequest() {
        this.status = 400;
        this.error = "Bad Request";
        return this;
    }

    public CustomResponse build() {
        return new CustomResponse(status, error, message, path);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CustomResponse(Object status, String error, String message, String path) { }
}
