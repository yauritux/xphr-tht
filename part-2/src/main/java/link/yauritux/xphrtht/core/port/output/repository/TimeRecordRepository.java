package link.yauritux.xphrtht.core.port.output.repository;

import link.yauritux.xphrtht.core.domain.entity.TimeRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
public interface TimeRecordRepository extends PagingAndSortingRepository<TimeRecord, Long> {
}
