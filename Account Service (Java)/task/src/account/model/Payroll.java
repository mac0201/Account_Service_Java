package account.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
// Add table unique constraint for columns employee and period
@Table(name = "payrolls", uniqueConstraints = { @UniqueConstraint(name = "EmployeeAndPeriod", columnNames = { "employee", "period"}) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Payroll {
    @Column(name = "payroll_id")
    @Id
    @SequenceGenerator(name = "payroll_seq", sequenceName = "payroll_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "payroll_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee")
    @ToString.Exclude
    private User employee;

    private String period;
    private long salary; // calculated in cents
}
