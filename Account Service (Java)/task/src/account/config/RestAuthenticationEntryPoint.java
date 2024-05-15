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
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

//        throw new Authent

        String requestPath = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        requestPath = requestPath == null ? request.getServletPath() : requestPath;


        var responseBuilder = ResponseBuilders.ErrorResponse.builder();
//        responseBuilder.status(401);
//        responseBuilder.error("Unauthorized");
        SecurityLog eventLog = null;




//        System.out.println("REQUEST PATH: " + requestPath);
//        System.out.println("REQUEST URI: " + request.getRequestURI());
        if (requestPath != null && !requestPath.equals("/favicon.ico")) { // h2-console
            String userEmail = retrieveAuthorizationEmail(request.getHeader("authorization"));
            if (!userEmail.equals("Anonymous")) {



//                responseBuilder.path(requestPath);

                System.err.println("SETTING PATH....");
                responseBuilder.path(requestPath);
                // Is account already locked?

                if (adminService.isUserNonLocked(userEmail)) {

                    attemptCounters.putIfAbsent(userEmail, 0);
                    int attemptCount = attemptCounters.get(userEmail) + 1;
                    attemptCounters.put(userEmail, attemptCount);

//            eventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, null, userEmail, requestPath);
                    eventLogger.handleSecurityEvent(SecurityEventType.LOGIN_FAILED, userEmail, requestPath, requestPath);


                    // lock account if more than 5 attempts
                    if (attemptCount == 5) {
                        // reset counter for user
                        attemptCounters.put(userEmail, 0);
                        eventLogger.handleSecurityEvent(SecurityEventType.BRUTE_FORCE, userEmail, requestPath, requestPath);
                        adminService.updateUserAccess("LOCK", userEmail, requestPath);
//                securityEventLogger.handleSecurityEvent(SecurityEventType.BRUTE_FORCE, null, userEmail, requestPath);
                    }


                }
            }
        } else if (requestPath == null) {
            System.err.println("Path null...");
        }

        responseBuilder.status(401);
        responseBuilder.error("Unauthorized");
        responseBuilder.message(authException.getMessage());

//        System.out.println(responseBuilder.build());



        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(responseBuilder.build()));
        response.getWriter().flush();
//        response.getOutputStream().flush();
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());




        //
//        response.setStatus(401);
//
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
