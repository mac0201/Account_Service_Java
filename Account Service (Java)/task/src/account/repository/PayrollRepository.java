package account.repository;

import account.model.Payroll;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PayrollRepository extends CrudRepository<Payroll, Long> {
    Optional<Payroll> findByEmployeeIdAndPeriod(long userId, String period);
    Stream<Payroll> findAllByEmployeeEmailAndPeriod(String email, String period);
    Stream<Payroll> findAllByEmployeeEmail(String email);
}
