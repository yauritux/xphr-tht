package link.yauritux.xphrtht.adapter.input.jspview;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.input.querysvc.IReportQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
class ReportWebControllerTest {

    @InjectMocks
    private ReportWebController reportWebController;

    @Mock
    private IReportQueryService reportQueryService;

    @Mock
    private Model model;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetTimeTrackingReport() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 2, 1, 0, 0);
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);
        List<EmployeeTimeTrackingReportDto> mockData = Arrays.asList(
                new EmployeeTimeTrackingReportDto("Yauri", "Next Gen Firewall", BigDecimal.valueOf(100.0)),
                new EmployeeTimeTrackingReportDto("Jacky", "SAP Integration", BigDecimal.valueOf(55.0))
        );
        Page<EmployeeTimeTrackingReportDto> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(userDetails.getUsername()).thenReturn("yauri");
        when(reportQueryService.getTimeTrackingReport(startDate, endDate, pageable)).thenReturn(mockPage);

        String viewName = reportWebController.getTimeTrackingReport(startDate, endDate, page, size, userDetails, model);

        assertEquals("work_hours_report", viewName);
        verify(model).addAttribute("username", "yauri");
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("pageSize", 5);
    }
}