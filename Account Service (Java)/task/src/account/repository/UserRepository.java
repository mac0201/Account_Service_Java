package account.repository;

import account.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
