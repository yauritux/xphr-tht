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

    @Query(value = "SELECT e.name AS employeeName, p.name AS projectName," +
            " SUM(EXTRACT(EPOCH FROM (tr.time_to - tr.time_from)) / 3600) AS totalHours " +
            "FROM time_records tr " +
            " JOIN employees e ON tr.employee_id = e.id " +
            " JOIN projects p ON tr.project_id = p.id " +
            "WHERE tr.time_from BETWEEN :startDate AND :endDate " +
            "GROUP BY e.name, p.name " +
            "ORDER BY e.name, p.name", nativeQuery = true)
    List<EmployeeTimeTrackingReportDto> getTimeTrackingReport(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
