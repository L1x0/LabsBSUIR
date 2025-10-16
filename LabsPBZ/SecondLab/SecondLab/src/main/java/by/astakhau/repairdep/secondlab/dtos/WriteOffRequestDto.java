package by.astakhau.repairdep.secondlab.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WriteOffRequestDto {
    private EquipmentRequestDto equipment;

    private LocalDate writeOffDate;

    private String reason;

    private String writeOffDocNumber;

    private EmployeeRequestDto createdBy;
}
