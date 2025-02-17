package link.yauritux.xphrtht.core.domain.service.query;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.port.output.repository.TimeRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
class ReportQueryServiceTest {

    @InjectMocks
    private ReportQueryService reportQueryService;

    @Mock
    private TimeRecordRepository timeRecordRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(timeRecordRepository.getTimeTrackingReport(startDate, endDate, pageable)).thenReturn(mockPage);

        Page<EmployeeTimeTrackingReportDto> result = reportQueryService.getTimeTrackingReport(startDate, endDate, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Yauri", result.getContent().get(0).employeeName());
        assertEquals("Next Gen Firewall", result.getContent().get(0).projectName());
        verify(timeRecordRepository).getTimeTrackingReport(startDate, endDate, pageable);
    }
}