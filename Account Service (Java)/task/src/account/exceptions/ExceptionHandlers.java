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
        ErrorResponse response = new ErrorResponse(ex.getStatusCode().value(), ex.getMessage(), request.getServletPath());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    //!! **************************************************** CUSTOM EXCEPTIONS
    @ExceptionHandler({RequestValidationException.class, UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRequestValidationException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(response);
    }

    //!! **************************************************** SPRING EXCEPTIONS
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getServletPath();
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), path);
        return ResponseEntity.badRequest().body(response);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        ErrorResponse response = new ErrorResponse(status.value(), ex.getMessage(), request.getDescription(false));
//        return ResponseEntity.badRequest().body(response);
//    }

    //
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseBody
//    private ResponseEntity<Object> handleCustomExceptions(RuntimeException ex, HttpServletRequest request) throws JsonProcessingException {
//        int statusCode = extractStatusCode(ex);
//        ErrorResponse responseObject = new ErrorResponse(statusCode, ex.getMessage(), request.getServletPath());
//        return ResponseEntity.status(statusCode).build();
////        return ResponseEntity.status(statusCode).body("{}");
//    }
//
//    private int extractStatusCode(RuntimeException ex) {
//        Class<? extends RuntimeException> exceptionClass = ex.getClass();
//        ResponseStatus annotation = exceptionClass.getAnnotation(ResponseStatus.class);
//        return annotation != null ? annotation.value().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
//    }

}
