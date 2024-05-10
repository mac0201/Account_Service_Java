package account.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import java.io.IOException;
import java.security.SecureRandom;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15, new SecureRandom());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
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
//                    .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
                    .requestMatchers(HttpMethod.POST, "/api/acct/payments").permitAll()
//                    .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")
                    .requestMatchers(HttpMethod.PUT, "/api/acct/payments").permitAll()
                    // authenticate other requests
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new RestAuthenticationEntryPoint())) // Handle auth errors
            .csrf().disable() // For Postman
            .headers().frameOptions().disable() // For the H2 console
            .and()
            .sessionManagement(sessions ->
                    sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no session, since REST is implemented
            .build();
    }

    static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }
}
