package account.repository;

import account.model.security.events.SecurityLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LogRepository extends CrudRepository<SecurityLog, Long> {
    List<SecurityLog> findAll();
}
