package by.astakhau.repairdep.secondlab.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "write_offs")
public class WriteOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    @ToString.Exclude
    private Equipment equipment;

    @Column(name = "writeoff_date", nullable = false)
    private LocalDate writeOffDate;

    private String reason;

    @Column(name = "writeoff_doc_number")
    private String writeOffDocNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    private Employee createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
