package link.yauritux.xphrtht.core.domain.dto;

import java.math.BigDecimal;

/**
 * @author Yauri Attamimi
 * @version 0.1
 * @param employeeName the employee name
 * @param projectName the project name
 * @param totalHours total hours spent by the employee on the project
 */
public record EmployeeTimeTrackingReportDto(String employeeName, String projectName, BigDecimal totalHours) {
}
