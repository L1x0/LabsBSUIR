package by.astakhau.repairdep.secondlab.mappers;

import by.astakhau.repairdep.secondlab.dtos.EmployeeRequestDto;
import by.astakhau.repairdep.secondlab.entities.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeRequestDto EntityToRequestDto(Employee employee) {
        return EmployeeRequestDto.builder()
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .lastName(employee.getLastName())
                .phone(employee.getPhone())
                .build();
    }
}
