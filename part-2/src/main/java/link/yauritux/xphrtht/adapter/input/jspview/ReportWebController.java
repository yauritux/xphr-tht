package link.yauritux.xphrtht.adapter.input.jspview;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

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
            @RequestParam(required = false) LocalDateTime endDate, Model model) {
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(1);
        }

        List<EmployeeTimeTrackingReportDto> reportData = reportQueryService.getTimeTrackingReport(startDate, endDate);

        model.addAttribute("reportData", reportData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "work_hours_report";
    }

}
