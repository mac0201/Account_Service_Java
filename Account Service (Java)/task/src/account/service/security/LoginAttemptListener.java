package account.service.security;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoginAttemptListener {

    private final LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        System.err.println("resetting counter for user...");
        loginAttemptService.resetCounter(event.getAuthentication().getName());
//        if (loginAttemptService.isBlocked(event.getAuthentication().getName())) {
//
//        }
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureLockedEvent event) {
//        System.err.println("Failure due to locked");
//        loginAttemptService.resetCounter(event.getAuthentication().getName());
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        System.err.println("Listener...");
        String attemptEmail = event.getAuthentication().getName();
        loginAttemptService.loginFailed(attemptEmail);
    }
}
