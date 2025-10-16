package by.astakhau.repairdep.secondlab.controllers;

import by.astakhau.repairdep.secondlab.dtos.EquipmentRequestDto;
import by.astakhau.repairdep.secondlab.dtos.EquipmentResponseDto;
import by.astakhau.repairdep.secondlab.dtos.WriteOffRequestDto;
import by.astakhau.repairdep.secondlab.dtos.WriteOffResponseDto;
import by.astakhau.repairdep.secondlab.entities.Equipment;
import by.astakhau.repairdep.secondlab.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/equipment-info")
    public ResponseEntity<Equipment> saveEquipment(@RequestBody EquipmentRequestDto equipmentRequestDto) {
        var equipment = companyService.saveEquipmentInfo(equipmentRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/equipment-info")
                .queryParam("id", equipment.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/equipment-info")
    public ResponseEntity<List<EquipmentResponseDto>> getEquipmentInfo() {
        return ResponseEntity.ok(companyService.getEquipments());
    }

    @PostMapping("/write-off")
    public ResponseEntity<Void> createWriteOff(@RequestBody WriteOffRequestDto writeOffRequestDto) {
        try {
            companyService.createWriteOff(writeOffRequestDto);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/write-off")
    public ResponseEntity<List<WriteOffResponseDto>> getWriteOff() {
        return ResponseEntity.ok(companyService.getWriteOffs());
    }
}
