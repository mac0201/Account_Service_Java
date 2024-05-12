package account.exceptions.definitions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {

    @Getter
    public static class ServiceException extends RuntimeException {
        private final int status;
        public ServiceException(String message, int status) { super(message); this.status = status; }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class RequestValidationException extends RuntimeException {
        public RequestValidationException() { super("Request parameter validation failed"); }
    }

}
