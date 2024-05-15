package account.repository;

import account.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
    boolean existsByEmailIgnoreCase(String email);
//    boolean isLocked(String email);
    boolean existsByEmailIgnoreCaseAndAccountLockedTrue(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
