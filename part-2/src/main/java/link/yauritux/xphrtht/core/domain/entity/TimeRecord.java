package link.yauritux.xphrtht.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Entity
@Table(name = "time_records")
@Data
public class TimeRecord {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private LocalDateTime timeFrom;
    private LocalDateTime timeTo;
}
