package by.astakhau.tests;

import by.astakhau.autotransportcompany.LegalDepartment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;

public class LegalDepartmentTest {
    @Test
    public void getterAndSetterTest(){
        LegalDepartment ld = new LegalDepartment(new ArrayList<>(), new ArrayList<>());
        ld.setClaims(new ArrayList<>(List.of("1", "2", "3", "4")));

        assertEquals("1", ld.getClaims().get(0));
    }
}
