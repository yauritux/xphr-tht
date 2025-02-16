package link.yauritux.xphrtht.adapter.input.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import link.yauritux.xphrtht.adapter.annotation.IsAuthenticatedUser;
import link.yauritux.xphrtht.adapter.common.PageReportFilter;
import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.domain.vo.UserRole;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Time Tracking Report API", description = "Employee Time Tracking (Working Hours) Report API")
@RequiredArgsConstructor
public class ReportApiController {

    private final IReportQueryService reportQueryService;

    @IsAuthenticatedUser
    @GetMapping
    public ResponseEntity<Page<EmployeeTimeTrackingReportDto>> getTimeTrackingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(auth ->
                auth.getAuthority().equalsIgnoreCase("ROLE_" + UserRole.ADMIN.name()));

        Page<EmployeeTimeTrackingReportDto> reportResults =
                reportQueryService.getTimeTrackingReport(startDate, endDate, pageable);

        return ResponseEntity.ok(new PageReportFilter(reportResults).getReportPage(
                userDetails.getUsername(), isAdmin, pageable));
    }
}
