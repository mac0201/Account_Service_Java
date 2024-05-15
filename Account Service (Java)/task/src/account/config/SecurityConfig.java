package account.config;

import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RestAuthenticationEntryPoint authEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
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
                // AUDITOR
                .requestMatchers(HttpMethod.GET, "/api/security/**").hasRole("AUDITOR")
                // authenticate all other requests
                .anyRequest().authenticated()
            );



//        http.httpBasic(Customizer.withDefaults());
        http.httpBasic().authenticationEntryPoint(authEntryPoint);
        http.exceptionHandling()
//                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
        http.csrf().disable();  // For Postman
        http.headers().frameOptions().disable(); // For H2 console
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Disable sessions

        return http.build();

    }



    @Component
    @AllArgsConstructor
    public static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        private final SecurityEventLogger eventLogger;
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            String user = SecurityContextHolder.getContext().getAuthentication().getName();
            eventLogger.handleSecurityEvent(SecurityEventType.ACCESS_DENIED, user, request.getServletPath(), request.getServletPath());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
        }
    }
}
