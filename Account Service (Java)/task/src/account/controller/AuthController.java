package account.controller;

import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // register user
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> signup(@RequestBody @Valid UserRegistrationDTO registration) {
        return ResponseEntity.ok(authService.registerUser(registration));
    }

    // change password
    @PostMapping("/changepass")
    public ResponseEntity<Object> changePassword(
            @Valid @RequestBody PasswordResetDTO reset,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(
                authService.updatePassword(reset, userDetails.getUsername()));
    }

}
