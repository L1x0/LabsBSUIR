package by.astakhau.repairdep.secondlab.mappers;

import by.astakhau.repairdep.secondlab.dtos.RepairOrderRequestDto;
import by.astakhau.repairdep.secondlab.entities.RepairOrder;

public class RepairOrderMapper {
    public RepairOrder requestDroToEntity(RepairOrderRequestDto repairOrderRequestDto) {
        return RepairOrder.builder()
                .id(null)
                .note("Процесс ремонта начат")
                .repairNumber(null)
                .build();
    }
}
