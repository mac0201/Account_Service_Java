package account.exceptions.definitions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import account.exceptions.definitions.CustomExceptions.ServiceException;

public class UserAuthExceptions {

    public static class AuthServiceException extends ServiceException {
        public AuthServiceException(String message, int status) { super(message, status); }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends AuthServiceException {
        public UserNotFoundException() { super("User not found!", 404); }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class UserAlreadyExistsException extends AuthServiceException {
        public UserAlreadyExistsException() { super("User exist!", 400); }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class PasswordValidationException extends AuthServiceException {
        public PasswordValidationException(String message) { super(message, 400); }
    }
}
