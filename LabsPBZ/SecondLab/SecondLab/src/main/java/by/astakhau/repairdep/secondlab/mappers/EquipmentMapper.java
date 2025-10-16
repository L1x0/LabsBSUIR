package by.astakhau.repairdep.secondlab.mappers;

import by.astakhau.repairdep.secondlab.dtos.EquipmentRequestDto;
import by.astakhau.repairdep.secondlab.dtos.EquipmentResponseDto;
import by.astakhau.repairdep.secondlab.entities.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {
    public Equipment equipmentFromDto(EquipmentRequestDto equipmentRequestDto) {
        return Equipment.builder()
                .id(null)
                .name(equipmentRequestDto.getName())
                .note(equipmentRequestDto.getNote())
                .model(equipmentRequestDto.getModel())
                .inventoryNumber(equipmentRequestDto.getInventoryNumber())
                .manufactureYear(equipmentRequestDto.getManufactureYear())
                .purchaseDate(equipmentRequestDto.getPurchaseDate())
                .status(equipmentRequestDto.getStatus())
                .build();
    }

    public EquipmentResponseDto equipmentResponseFromEntity(Equipment equipment) {
        return EquipmentResponseDto.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .note(equipment.getNote())
                .model(equipment.getModel())
                .inventoryNumber(equipment.getInventoryNumber())
                .manufactureYear(equipment.getManufactureYear())
                .purchaseDate(equipment.getPurchaseDate())
                .status(equipment.getStatus())
                .build();
    }

    public EquipmentRequestDto equipmentRequestFromEntity(Equipment equipment) {
        return EquipmentRequestDto.builder()
                .name(equipment.getName())
                .note(equipment.getNote())
                .model(equipment.getModel())
                .inventoryNumber(equipment.getInventoryNumber())
                .manufactureYear(equipment.getManufactureYear())
                .purchaseDate(equipment.getPurchaseDate())
                .status(equipment.getStatus())
                .build();
    }
}
