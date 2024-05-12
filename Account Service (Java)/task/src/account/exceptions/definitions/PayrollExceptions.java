package account.exceptions.definitions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import account.exceptions.definitions.CustomExceptions.ServiceException;

public class PayrollExceptions {

    public static class PayrollServiceException extends ServiceException {
        public PayrollServiceException(String message, int status) { super(message, status); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class PayrollNotFoundException extends PayrollServiceException {
        public PayrollNotFoundException(String message) { super(message, 404); }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class PayrollPeriodParseException extends PayrollServiceException {
        public PayrollPeriodParseException(String message) { super("Problem parsing payroll periods - " + message, 500); }
    }
}
