package account.exceptions.definitions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import account.exceptions.definitions.CustomExceptions.ServiceException;

public class AdminExceptions {

    public static class AdminServiceException extends ServiceException {
        public AdminServiceException(String message, int status) { super(message, status); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DeleteAdminException extends AdminServiceException {
        public DeleteAdminException() { super("Can't remove ADMINISTRATOR role!", 400); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class UserRoleNotAssigned extends AdminServiceException {
        public UserRoleNotAssigned() { super("The user does not have a role!", 400); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class LastRoleException extends AdminServiceException {
        public LastRoleException() { super("The user must have at least one role!", 400); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class RoleGroupConstraintValidationException extends AdminServiceException {
        public RoleGroupConstraintValidationException() { super("The user cannot combine administrative and business roles!", 400); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class RoleNotFoundException extends AdminServiceException {
        public RoleNotFoundException() { super("Role not found!", 404); }
    }
}
