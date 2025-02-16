package link.yauritux.xphrtht.core.port.input.querysvc;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
public interface IReportQueryService {

    List<EmployeeTimeTrackingReportDto> getTimeTrackingReport(LocalDateTime start, LocalDateTime endDate);
}