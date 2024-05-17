package account.model.security.events;

import account.repository.LogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SecurityEventLogger {
    private final LogRepository logRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(SecurityEventLogger.class);
    private HttpServletRequest httpRequest;

    public void handleSecurityEvent(SecurityEventType action, String subject, String object) {
        String path = httpRequest.getRequestURI();
        LOGGER.info("Handling security event {} on path \"{}\"", action, path);
        SecurityLog log = SecurityLog.builder()
                .date(System.currentTimeMillis())
                .action(action)
                .subject(subject == null || subject.isEmpty() ? "Anonymous" : subject)
                .object(object)
                .path(path == null ? "path" : path)
                .build();
        logRepository.save(log);
    }
}
