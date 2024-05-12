package account.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15, new SecureRandom());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // HTTP AUTH ENDPOINTS
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(toH2Console()).permitAll()
                .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                // AUTH
                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyRole(
                        "USER", "ACCOUNTANT", "ADMINISTRATOR")
                // ADMIN
                .requestMatchers( "/api/admin/**").hasRole("ADMINISTRATOR")
                // PAYROLL
                .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
                .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")
                // authenticate all other requests
                .anyRequest().authenticated()
            );

        http.exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());

        http.httpBasic(Customizer.withDefaults());
        http.csrf().disable();  // For Postman
        http.headers().frameOptions().disable(); // For H2 console
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Disable sessions

        return http.build();

    }

    static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

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

    public static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
        }
    }
}
