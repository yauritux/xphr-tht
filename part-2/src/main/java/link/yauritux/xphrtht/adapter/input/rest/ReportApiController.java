package link.yauritux.xphrtht.adapter.input.rest;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportApiController {

    private final IReportQueryService reportQueryService;

    @GetMapping
    public ResponseEntity<List<EmployeeTimeTrackingReportDto>> getTimeTrackingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(1);
        }

        return ResponseEntity.ok(reportQueryService.getTimeTrackingReport(startDate, endDate));
    }
}
