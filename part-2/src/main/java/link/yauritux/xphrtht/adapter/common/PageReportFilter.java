package link.yauritux.xphrtht.adapter.common;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
public class PageReportFilter {

    private final Page<EmployeeTimeTrackingReportDto> reportPage;

    public PageReportFilter(Page<EmployeeTimeTrackingReportDto> reportPage) {
        this.reportPage = reportPage;
    }

    public Page<EmployeeTimeTrackingReportDto> getReportPage(String username, boolean isAdmin, Pageable pageable) {
        if (!isAdmin) {
            return new PageImpl<>(this.reportPage.getContent().stream()
                    .filter(r -> r.employeeName().equalsIgnoreCase(username))
                    .toList(), pageable, this.reportPage.getTotalElements());
        }

        return this.reportPage;
    }
}
