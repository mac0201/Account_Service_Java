package account.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import account.exceptions.CustomExceptions.*;


@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    // Handler specifically for ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) { //, ResponseStatus responseStatus
        ErrorResponse response = new ErrorResponse(ex.getStatusCode().value(), ex.getStatusCode().toString(), ex.getMessage(), request.getServletPath());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    //!! **************************************************** CUSTOM EXCEPTIONS
    @ExceptionHandler({RequestValidationException.class, UserAlreadyExistsException.class, PasswordValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRequestValidationException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(response);
    }

    //!! **************************************************** SPRING EXCEPTIONS
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getServletPath();
        ErrorResponse response = new ErrorResponse(400, "Bad Request", ex.getMessage(), path);
        return ResponseEntity.badRequest().body(response);
    }

}
