package link.yauritux.xphrtht.core.port.output.repository;

import link.yauritux.xphrtht.core.domain.entity.Employee;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
}
