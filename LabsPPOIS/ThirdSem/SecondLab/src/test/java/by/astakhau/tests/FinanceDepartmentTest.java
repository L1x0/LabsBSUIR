package by.astakhau.tests;

import by.astakhau.autotransportcompany.FinanceDepartment;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FinanceDepartmentTest {
    @Test
    public void getterAndSetterTest() {
        FinanceDepartment fd = new FinanceDepartment(new ArrayList<>(), 1000);
        fd.setBudget(100);
        assertEquals(100, fd.getBudget());
    }
}
