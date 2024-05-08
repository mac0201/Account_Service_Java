package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException() { super("User not found!"); }
    }

    // METHOD 2
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException() {
            super("User exist!");
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class RequestValidationException extends RuntimeException {
        public RequestValidationException() { super("Request parameter validation failed"); }
    }


}
