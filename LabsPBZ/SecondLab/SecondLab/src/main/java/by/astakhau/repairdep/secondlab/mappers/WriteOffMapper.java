package by.astakhau.repairdep.secondlab.mappers;

import by.astakhau.repairdep.secondlab.dtos.WriteOffRequestDto;
import by.astakhau.repairdep.secondlab.dtos.WriteOffResponseDto;
import by.astakhau.repairdep.secondlab.entities.WriteOff;
import by.astakhau.repairdep.secondlab.repositories.EmployeeRepository;
import by.astakhau.repairdep.secondlab.repositories.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class WriteOffMapper {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public WriteOff requestDtoToWriteOff(WriteOffRequestDto writeOffRequestDto) {
        var equipment = equipmentRepository.findByInventoryNumber(writeOffRequestDto.getEquipment().getInventoryNumber());

        var employee = employeeRepository.findByFirstNameAndPhone(writeOffRequestDto.getCreatedBy().getFirstName(),
                writeOffRequestDto.getCreatedBy().getPhone());

        if (equipment.isEmpty() || employee.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        return WriteOff.builder()
                .id(null)
                .writeOffDocNumber(writeOffRequestDto.getWriteOffDocNumber())
                .writeOffDate(writeOffRequestDto.getWriteOffDate())
                .equipment(equipment.get())
                .createdAt(OffsetDateTime.now())
                .createdBy(employee.get())
                .reason(writeOffRequestDto.getReason())
                .build();
    }

    public WriteOffRequestDto writeOffToRequestDto(WriteOff writeOff) {
        return WriteOffRequestDto.builder()
                .writeOffDocNumber(writeOff.getWriteOffDocNumber())
                .writeOffDate(writeOff.getWriteOffDate())
                .equipment(equipmentMapper.equipmentRequestFromEntity(writeOff.getEquipment()))
                .createdBy(employeeMapper.EntityToRequestDto(writeOff.getCreatedBy()))
                .reason(writeOff.getReason())
                .build();
    }

    public WriteOffResponseDto writeOffToResponseDto(WriteOff writeOff) {
        return WriteOffResponseDto.builder()
                .id(writeOff.getId())
                .writeOffDocNumber(writeOff.getWriteOffDocNumber())
                .writeOffDate(writeOff.getWriteOffDate())
                .equipment(equipmentMapper.equipmentRequestFromEntity(writeOff.getEquipment()))
                .createdBy(employeeMapper.EntityToRequestDto(writeOff.getCreatedBy()))
                .reason(writeOff.getReason())
                .writeOffDocNumber(writeOff.getWriteOffDocNumber())
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
