package by.astakhau.repairdep.secondlab.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "repair_orders")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "repair_number", unique = true)
    private String repairNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    @ToString.Exclude
    private Equipment equipment;

    @Column(name = "date_submitted", nullable = false)
    private LocalDate dateSubmitted;

    @Column(name = "repair_type")
    private String repairType;

    @Column(name = "expected_finish_date")
    private LocalDate expectedFinishDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_employee_id")
    @ToString.Exclude
    private Employee submittedByEmployee;

    @Column(name = "submitted_by_personnel_num")
    private String submittedByPersonnelNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by_employee_id")
    @ToString.Exclude
    private Employee acceptedByEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_performer_employee_id")
    @ToString.Exclude
    private Employee repairPerformerEmployee;

    @Column(name = "performer_position")
    private String performerPosition;

    private String status;

    private String note;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<RepairSparePart> spareParts = new HashSet<>();
}
