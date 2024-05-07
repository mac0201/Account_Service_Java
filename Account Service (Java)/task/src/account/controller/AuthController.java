package account.controller;

import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ObjectMapper objectMapper; // For mapping object to JSON strings and vice-versa

    public AuthController(AuthService authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    // register user
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody UserRegistrationDTO registration, BindingResult bindingResult) throws JsonProcessingException, MethodArgumentNotValidException {
        if(bindingResult.hasErrors()) throw new MethodArgumentNotValidException(null, bindingResult);

//        User user = authService.registerUser(registration);
        return ResponseEntity.ok(new User(1, registration.getName(), registration.getLastname(), registration.getEmail(), registration.getPassword()));
    }

    // change password
    @PostMapping("/changepass")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordResetDTO reset) throws JsonProcessingException {
        User user = authService.updatePassword(reset);
        return ResponseEntity.ok().build();
    }


}
