package account.config;

import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.service.AdminService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityEventLogger eventLogger;
    private final AdminService adminService;
    private final Map<String, Integer> attemptCounters = new ConcurrentHashMap<>();
    private final SecurityEventLogger securityEventLogger;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestPath = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (requestPath != null && !requestPath.equals("/favicon.ico")) { // h2-console
            String userEmail = retrieveAuthorizationEmail(request.getHeader("authorization"));
            attemptCounters.putIfAbsent(userEmail, 0);
            int attemptCount = attemptCounters.get(userEmail) + 1;
            attemptCounters.put(userEmail, attemptCount);
            eventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, null, userEmail, requestPath);
            // lock account if more than 5 attempts
            if (attemptCount == 5) {
                // reset counter for user
                attemptCounters.put(userEmail, 0);
                adminService.updateUserAccess("LOCK", userEmail, requestPath);
                securityEventLogger.handleSecurityEvent(SecurityEventType.BRUTE_FORCE, null, userEmail, requestPath);
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    private String retrieveAuthorizationEmail(String authHeader) {
        if (authHeader == null) return "Anonymous";
        //! Overview:
        // Basic dXNlcjFAYWNtZS5jb206cGFzc3dvcmQxMjM0NTY2  ->  dXNlcjFAYWNtZS5jb206cGFzc3dvcmQxMjM0NTY2  ->  mail@mail.com:password  ->  mail@mail.com
        String encodedBase64 = authHeader.split(" ")[1];
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBase64);
        String credentials = new String(decodedBytes);
        return credentials.split(":")[0];
    }
}
