package by.astakhau.repairdep.secondlab.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class EquipmentRequestDto {
    private String inventoryNumber;

    private String name;

    private String model;

    private Integer manufactureYear;

    private LocalDate purchaseDate;

    private String status;

    private String note;
}
