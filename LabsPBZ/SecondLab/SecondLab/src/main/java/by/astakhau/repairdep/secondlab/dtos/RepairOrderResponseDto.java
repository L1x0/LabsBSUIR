package by.astakhau.repairdep.secondlab.dtos;

import by.astakhau.repairdep.secondlab.entities.Employee;
import by.astakhau.repairdep.secondlab.entities.Equipment;

import java.time.LocalDate;
import java.time.OffsetDateTime;


public class RepairOrderResponseDto {
    private Integer id;

    private String repairNumber;

    private Equipment equipment;

    private LocalDate dateSubmitted;

    private String repairType;

    private Employee submittedByEmployee;

    private String submittedByPersonnelNum;

    private Employee acceptedByEmployee;

    private Employee repairPerformerEmployee;

    private String status;

    private String note;

    private OffsetDateTime createdAt;

}
