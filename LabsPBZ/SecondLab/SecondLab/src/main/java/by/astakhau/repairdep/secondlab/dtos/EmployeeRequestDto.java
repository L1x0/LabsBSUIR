package by.astakhau.repairdep.secondlab.dtos;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EmployeeRequestDto {

    private String lastName;

    private String firstName;

    private String middleName;

    private String phone;

    private String email;
}
