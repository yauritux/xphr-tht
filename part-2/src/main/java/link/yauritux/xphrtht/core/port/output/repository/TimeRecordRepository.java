package link.yauritux.xphrtht.core.port.output.repository;

import link.yauritux.xphrtht.core.domain.dto.EmployeeTimeTrackingReportDto;
import link.yauritux.xphrtht.core.domain.entity.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {

    @Query("SELECT e.name AS employeeName, p.name AS projectName," +
            "  SUM(EXTRACT(EPOCH FROM (tr.timeTo - tr.timeFrom)) / 3600) AS totalHours " +
            "FROM TimeRecord tr " +
            "JOIN Employee e ON tr.employee.id = e.id " +
            "JOIN Project p ON tr.project.id = p.id " +
            "WHERE tr.timeFrom BETWEEN :startDate AND :endDate " +
            "GROUP BY e.employeeName, p.projectName " +
            "ORDER BY e.employeeName, p.projectName")
    List<EmployeeTimeTrackingReportDto> getTimeTrackingReport(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
