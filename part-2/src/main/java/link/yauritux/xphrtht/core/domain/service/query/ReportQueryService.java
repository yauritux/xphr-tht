package link.yauritux.xphrtht.core.domain.service.query;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import link.yauritux.xphrtht.core.port.output.repository.TimeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
public class ReportQueryService implements IReportQueryService {

    private final TimeRecordRepository timeRecordRepository;

    @Override
    public Page<EmployeeTimeTrackingReportDto> getTimeTrackingReport(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null && endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(1);
        } else if (startDate == null) {
            startDate = endDate.minusMonths(1);
        } else if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return timeRecordRepository.getTimeTrackingReport(startDate, endDate, pageable);
    }
}
