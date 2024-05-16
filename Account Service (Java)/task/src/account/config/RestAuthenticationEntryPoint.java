package account.config;

import account.model.User;
import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.model.security.events.SecurityLog;
import account.service.AdminService;
import account.util.ResponseBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityEventLogger eventLogger;
    private final AdminService adminService;
    private final Map<String, Integer> attemptCounters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // handle exception when accessing account that does not exist
        String attemptEmail = retrieveAuthorizationEmail(request.getHeader("Authorization"));
        String requestPath = request.getServletPath();

        // if email does not end with @acme, skip logging
        if (!attemptEmail.endsWith("@acme.com") && !requestPath.equals("/error") && !attemptEmail.equals("Anonymous")) {
            eventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, attemptEmail, requestPath, requestPath);
        }

        response.setStatus(401);
        response.setContentType("application/json");

        var resp = ResponseBuilders.ErrorResponse.builder().status(401).error("Unauthorized").message(authException.getMessage()).path(requestPath).build();

        response.getWriter().write(objectMapper.writeValueAsString(resp));
        response.getWriter().flush();

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
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
