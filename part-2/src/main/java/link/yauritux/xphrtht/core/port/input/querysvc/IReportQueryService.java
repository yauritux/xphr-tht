package link.yauritux.xphrtht.core.port.input.querysvc;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
public interface IReportQueryService {

    Page<EmployeeTimeTrackingReportDto> getTimeTrackingReport(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}