package account.exceptions;

import account.util.ResponseBuilders.ErrorResponse;
import account.exceptions.definitions.CustomExceptions.*;

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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    // !! SERVICE EXCEPTIONS ****************************************************
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex, HttpServletRequest request) {
        int status = ex.getStatus();
        var response = ErrorResponse.builder()
                .status(status)
                .error(HttpStatus.valueOf(status).getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getServletPath())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    //!! **************************************************** CUSTOM EXCEPTIONS
    @ExceptionHandler({
            ConstraintViolationException.class,
            JdbcSQLIntegrityConstraintViolationException.class,
            RequestValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRequestValidationException(RuntimeException ex, HttpServletRequest request) {
        var response = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getServletPath())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    //!! **************************************************** SPRING EXCEPTIONS
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = extractPathFromWebRequest(request);
        var response = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(ex.getMessage())
                .path(path)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = extractPathFromWebRequest(request);
        var response = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(ex.getMessage())
                .path(path)
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
        var response = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(errors)
                .path(path)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    private String extractPathFromWebRequest(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getServletPath();
    }
}
