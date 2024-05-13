package account.model.security.events;

import org.springframework.stereotype.Component;

@Component
public class SecurityEventLogger {

    public void handleSecurityEvent(SecurityLog log) {
        System.out.println("HANDLING EVENT: " + log);
    }


}
