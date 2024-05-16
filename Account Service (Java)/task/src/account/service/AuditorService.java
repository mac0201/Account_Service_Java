package account.service;

import account.model.security.events.SecurityLog;
import account.repository.LogRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuditorService {

    private final LogRepository logRepository;
    private final Logger appLogger;

    public List<SecurityLog> findAllLogs() {
        String auditor = SecurityContextHolder.getContext().getAuthentication().getName();
        appLogger.warn("Security logs accessed by: {}", auditor);
        return logRepository.findAll();
    }
}
