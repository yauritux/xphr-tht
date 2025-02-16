package link.yauritux.xphrtht.core.domain.service.query;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import link.yauritux.xphrtht.core.port.output.repository.TimeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
public class ReportQueryService implements IReportQueryService {

    private final TimeRecordRepository timeRecordRepository;

    @Override
    public List<EmployeeTimeTrackingReportDto> getTimeTrackingReport(LocalDateTime start, LocalDateTime endDate) {
        return List.of();
    }
}
