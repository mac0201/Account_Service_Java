package account.exceptions;

import account.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import account.exceptions.CustomExceptions.*;

import java.util.stream.Collectors;


@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    private final ResponseBuilder responseBuilder = new ResponseBuilder();

    // Handler specifically for ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) { //, ResponseStatus responseStatus
        var response = responseBuilder
                .setStatus(ex.getStatusCode().value())
                .setError(ex.getStatusCode().toString())
                .setMessage(ex.getMessage())
                .setPath(request.getServletPath())
                .build();
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    //!! **************************************************** CUSTOM EXCEPTIONS
    @ExceptionHandler({
            ConstraintViolationException.class,
            JdbcSQLIntegrityConstraintViolationException.class,
            RequestValidationException.class,
            UserAlreadyExistsException.class,
            PasswordValidationException.class,
            PayrollNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRequestValidationException(RuntimeException ex, HttpServletRequest request) {
        var response = responseBuilder
                .badRequest()
                .setMessage(ex.getMessage())
                .setPath(request.getServletPath())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    //!! **************************************************** SPRING EXCEPTIONS
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = extractPathFromWebRequest(request);
        var response = responseBuilder
                .badRequest()
                .setMessage(ex.getMessage())
                .setPath(path)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = extractPathFromWebRequest(request);
        var response = responseBuilder
                .badRequest()
                .setMessage(ex.getMessage())
                .setPath(path)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = extractPathFromWebRequest(request);
        // Get and join all error messages into a single string
        String errors = ex.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" ; "));
        var response = responseBuilder
                .setStatus(400)
                .setError("Bad Request")
                .setMessage(errors)
                .setPath(path)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    private String extractPathFromWebRequest(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getServletPath();
    }
}
