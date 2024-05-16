package account.service.security;

import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class LoginAttemptService {

    private final SecurityEventLogger securityEventLogger;
    private final AdminService adminService;

    public static final int MAX_ATTEMPT = 5;
    private Map<String, Integer> attemptsCache;

    private final HttpServletRequest request;


    public void loginFailed(String userEmail) {
        int attempts;
        System.out.println("BLOCKED? " + isBlocked(userEmail));
        try {
            attempts = attemptsCache.get(userEmail);
        } catch (NullPointerException ex) {
            attempts = 0;
        }

        attempts++;
        attemptsCache.put(userEmail, attempts);

        // log failed login attempt
        String requestPath = request.getRequestURI();
        securityEventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, userEmail, requestPath, requestPath);


        System.err.println("Attempts: " + attempts);

        if (attempts == MAX_ATTEMPT) {
            System.err.println("LOCKING USER: " + userEmail);
            securityEventLogger.handleSecurityEvent(SecurityEventType.BRUTE_FORCE, userEmail, requestPath, requestPath);
            // block user from admin service
            adminService.updateUserAccess("LOCK", userEmail, requestPath);
//
//            if (!isBlocked(userEmail)) {
//                securityEventLogger.handleSecurityEvent(SecurityEventType.BRUTE_FORCE, userEmail, requestPath, requestPath);
//                // block user from admin service
//                adminService.updateUserAccess("LOCK", userEmail, requestPath);
//            }
//            else {
//                System.out.println("User blocked!");
//            }
//
//            // is user unlocked?
//            // log brute force
//
//            attemptsCache.remove(userEmail);
        }
    }

    public boolean isBlocked(String userEmail) {
        if (attemptsCache.containsKey(userEmail)) {
            return attemptsCache.get(userEmail) >= MAX_ATTEMPT;
        }
        return false;
    }

    public void resetCounter(String userEmail) {
        System.err.println("Resetting login counter for " + userEmail);
        attemptsCache.remove(userEmail);
    }

}
