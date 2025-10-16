package by.astakhau.repairdep.secondlab.services;


import by.astakhau.repairdep.secondlab.dtos.EquipmentRequestDto;
import by.astakhau.repairdep.secondlab.dtos.EquipmentResponseDto;
import by.astakhau.repairdep.secondlab.dtos.WriteOffRequestDto;
import by.astakhau.repairdep.secondlab.dtos.WriteOffResponseDto;
import by.astakhau.repairdep.secondlab.entities.Equipment;
import by.astakhau.repairdep.secondlab.mappers.EquipmentMapper;
import by.astakhau.repairdep.secondlab.mappers.WriteOffMapper;
import by.astakhau.repairdep.secondlab.repositories.EquipmentRepository;
import by.astakhau.repairdep.secondlab.repositories.WriteOffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    private final WriteOffRepository writeOffRepository;
    private final WriteOffMapper writeOffMapper;

    public Equipment saveEquipmentInfo(EquipmentRequestDto equipmentRequestDto) {

        var equipment = equipmentRepository.findByInventoryNumber(equipmentRequestDto.getInventoryNumber());

        if (equipment.isPresent()) {
            equipment.get().setNote(equipmentRequestDto.getNote());
            equipment.get().setStatus(equipmentRequestDto.getStatus());

            return equipmentRepository.save(equipment.get());
        } else {
            return equipmentRepository.save(equipmentMapper.equipmentFromDto(equipmentRequestDto));
        }
    }

    public List<EquipmentResponseDto> getEquipments() {
        return equipmentRepository.findAll().stream()
                .map(equipmentMapper::equipmentResponseFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createWriteOff(WriteOffRequestDto writeOffRequestDto) {
        try {
            var writeOff = writeOffMapper.requestDtoToWriteOff(writeOffRequestDto);
            writeOffRepository.save(writeOff);

            var equipment = writeOff.getEquipment();
            equipment.setNote("Списан " + OffsetDateTime.now());

            equipmentRepository.save(equipment);
        } catch (HttpClientErrorException e) {
            log.error("Не нашёл технику");
            throw e;
        }
    }

    public List<WriteOffResponseDto> getWriteOffs() {
        return writeOffRepository.findAll().stream()
                .map(writeOffMapper::writeOffToResponseDto)
                .collect(Collectors.toList());
    }

    public void createRepairOrder()
}
