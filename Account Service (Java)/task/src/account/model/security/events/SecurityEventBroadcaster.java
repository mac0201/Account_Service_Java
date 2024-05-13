package account.model.security.events;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SecurityEventBroadcaster {

    private final SecurityEventLogger securityEventLogger;

    public void broadcastSecurityEvent(SecurityLog securityLog) {
        System.out.println("Broadcasting event...");
        securityEventLogger.handleSecurityEvent(securityLog);

    }

    public void broadcastSecurityEvent(SecurityEventType action, String subject, String object, String path) {
        SecurityLog log = SecurityLog.builder()
                .date(System.currentTimeMillis())
                .action(action)
                .subject(subject == null ? "Anonymous" : subject)
                .object(object)
                .path(path)
                .build();
        securityEventLogger.handleSecurityEvent(log);
    }


}
