package account.service.security;

import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class LoginAttemptService {

    private final SecurityEventLogger securityEventLogger;
    private final Logger logger;
    private final AdminService adminService;

    public static final int MAX_ATTEMPT = 5;
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final HttpServletRequest request;

    public void loginFailed(String userEmail) {
        int attempts;
        try {
            attempts = attemptsCache.get(userEmail);
        } catch (NullPointerException ex) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(userEmail, attempts);

        // log failed login attempt
        String requestPath = request.getRequestURI();
        securityEventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, userEmail, requestPath);

        if (attempts == MAX_ATTEMPT) {
            logger.info("Locking user {} due to too many failed login attempts", userEmail);
            securityEventLogger.handleSecurityEvent(SecurityEventType.BRUTE_FORCE, userEmail, requestPath);
            adminService.updateUserAccess("LOCK", userEmail, requestPath); // lock user account
        }
    }

    public void resetCounter(String userEmail) {
        logger.info("(LOGIN SUCCESS) Resetting login attempt counter for user {}", userEmail);
        attemptsCache.remove(userEmail);
    }

}
