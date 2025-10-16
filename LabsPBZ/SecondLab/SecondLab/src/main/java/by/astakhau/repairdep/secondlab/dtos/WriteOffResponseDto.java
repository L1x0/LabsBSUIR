package by.astakhau.repairdep.secondlab.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class WriteOffResponseDto {
    private Integer id;

    private EquipmentRequestDto equipment;

    private LocalDate writeOffDate;

    private String reason;

    private String writeOffDocNumber;

    private EmployeeRequestDto createdBy;

    private OffsetDateTime createdAt;
}
