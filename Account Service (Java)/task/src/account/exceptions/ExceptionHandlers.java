package account.exceptions;

import account.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.http.HttpResponse;

@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse response = new ErrorResponse(status.value(), "Parameter validation failed", request.getDescription(false));
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    private ResponseEntity<Object> handleCustomExceptions(RuntimeException ex, HttpServletRequest request) throws JsonProcessingException {
        int statusCode = extractStatusCode(ex);
        ErrorResponse responseObject = new ErrorResponse(statusCode, ex.getMessage(), request.getServletPath());
        return ResponseEntity.status(statusCode).build();
//        return ResponseEntity.status(statusCode).body("{}");
    }

    private int extractStatusCode(RuntimeException ex) {
        Class<? extends RuntimeException> exceptionClass = ex.getClass();
        ResponseStatus annotation = exceptionClass.getAnnotation(ResponseStatus.class);
        return annotation != null ? annotation.value().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

}
