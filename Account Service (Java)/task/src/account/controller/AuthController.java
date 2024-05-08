package account.controller;

import account.exceptions.CustomExceptions;
import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // register user
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody UserRegistrationDTO registration, BindingResult bindingResult, HttpServletRequest req) throws JsonProcessingException, MethodArgumentNotValidException {
        if(bindingResult.hasErrors()) throw new CustomExceptions.RequestValidationException();
        User user = authService.registerUser(registration);
        return ResponseEntity.ok(user);
    }

    // change password
    @PostMapping("/changepass")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody PasswordResetDTO reset, @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        User user = authService.updatePassword(reset, userDetails.getUsername());
        //! later - confirm password provided in request matches current password, then update
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        authService.testUser();
        return ResponseEntity.ok().build();
    }

}
