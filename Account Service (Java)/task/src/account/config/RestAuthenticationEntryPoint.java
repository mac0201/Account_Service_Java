package account.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    Map<String, Integer> attemptCounter = new ConcurrentHashMap<>();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String userEmail = retrieveAuthorizationEmail(request.getHeader("authorization"));
        attemptCounter.putIfAbsent(userEmail, 0);
        int i = attemptCounter.get(userEmail);
        attemptCounter.replace(userEmail, i + 1);

//            if (i+1 >= )


//            if (attemptCounter.containsKey(userEmail)) {
//
//            }

//            attemptCounter.
//            attemptCounter.computeIfPresent(userEmail, i -> i.);

        System.out.println("Attempting to log in: " + userEmail + "   total attempts: " + attemptCounter.get(userEmail));

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    private String retrieveAuthorizationEmail(String authHeader) {
        if (authHeader == null) throw new RuntimeException();
        //! Overview:
        // Basic dXNlcjFAYWNtZS5jb206cGFzc3dvcmQxMjM0NTY2  ->  dXNlcjFAYWNtZS5jb206cGFzc3dvcmQxMjM0NTY2  ->  mail@mail.com:password  ->  mail@mail.com
        String encodedBase64 = authHeader.split(" ")[1];
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBase64);
        String credentials = new String(decodedBytes);
        return credentials.split(":")[0];
    }
}
