package by.astakhau.repairdep.secondlab.dtos;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EquipmentResponseDto {
    private Integer id;

    private String inventoryNumber;

    private String name;

    private String model;

    private Integer manufactureYear;

    private LocalDate purchaseDate;

    private String status;

    private String note;
}
