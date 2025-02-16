package link.yauritux.xphrtht.adapter.input.jspview;

import link.yauritux.xphrtht.adapter.annotation.IsAuthenticatedUser;
import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.domain.vo.UserRole;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Controller
@RequestMapping("/web/reports")
@RequiredArgsConstructor
public class ReportWebController {

    private final IReportQueryService reportQueryService;

    @IsAuthenticatedUser
    @GetMapping("/work_hours")
    public String getTimeTrackingReport(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(1);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeTimeTrackingReportDto> reportData =
                reportQueryService.getTimeTrackingReport(startDate, endDate, pageable);

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase("ROLE_" + UserRole.ADMIN.name()));

        if (!isAdmin) {
            var username = userDetails.getUsername();
            reportData = new PageImpl<>(reportData.getContent().stream()
                    .filter(r -> r.employeeName().equalsIgnoreCase(username))
                    .toList(), pageable, reportData.getTotalElements());
        }

        model.addAttribute("reportData", reportData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("role", (isAdmin ? UserRole.ADMIN.name() : UserRole.EMPLOYEE.name()));

        return "work_hours_report";
    }

}
