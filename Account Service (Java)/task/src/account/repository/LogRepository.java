package account.repository;

import account.model.security.events.SecurityLog;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<SecurityLog, Long> { }
