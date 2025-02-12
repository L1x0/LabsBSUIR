package by.astakhau.tests;

import by.astakhau.autotransportcompany.SecurityDepartment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;

public class SecurityDepartmentTest {
    @Test
    public void getterAndSetterTest() {
        SecurityDepartment sd = new SecurityDepartment(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        sd.setInstruments(new ArrayList<>(List.of("1")));
        sd.setLogOfReflectedAttacks(new ArrayList<>(List.of("2")));

        assertEquals("1", sd.getInstruments().get(0));
        assertEquals("2", sd.getLogOfReflectedAttacks().get(0));
    }
}
