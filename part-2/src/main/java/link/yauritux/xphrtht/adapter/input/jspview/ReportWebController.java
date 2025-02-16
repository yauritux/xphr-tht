package link.yauritux.xphrtht.adapter.input.jspview;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/work_hours")
    public String getTimeTrackingReport(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Model model) {
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(1);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeTimeTrackingReportDto> reportData =
                reportQueryService.getTimeTrackingReport(startDate, endDate, pageable);

        model.addAttribute("reportData", reportData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "work_hours_report";
    }

}
