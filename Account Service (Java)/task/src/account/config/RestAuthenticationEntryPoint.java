package account.config;

import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.util.ResponseBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
@AllArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityEventLogger eventLogger;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // handle exception when accessing account that does not exist
        String attemptEmail = retrieveAuthorizationEmail(request.getHeader("Authorization"));
        String requestPath = request.getServletPath();

        // if email does not end with @acme, skip logging (temporary solution)
        if (!attemptEmail.endsWith("@acme.com") && !attemptEmail.equals("Anonymous") && !requestPath.equals("/error")) {
            eventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, attemptEmail, requestPath);
        }

        response.setStatus(401);
        response.setContentType("application/json");
        var body = ResponseBuilders.ErrorResponse.builder()
                .status(401).error("Unauthorized").message(authException.getMessage()).path(requestPath)
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
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
