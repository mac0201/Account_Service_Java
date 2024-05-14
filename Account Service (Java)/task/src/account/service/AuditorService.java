package account.service;

import account.model.security.events.SecurityLog;
import account.repository.LogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuditorService {

    private final LogRepository logRepository;

    public List<SecurityLog> findAllLogs() {
        return logRepository.findAll();
    }

}
