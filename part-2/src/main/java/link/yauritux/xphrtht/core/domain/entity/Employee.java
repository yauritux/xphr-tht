package link.yauritux.xphrtht.core.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "employee")
    private List<TimeRecord> timeRecords;
}
