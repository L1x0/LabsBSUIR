package by.astakhau.tests;

import by.astakhau.autotransportcompany.Employee;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    @Test
    public void getterAndSetterTest() {

        Employee e = new Employee("artsiom", 19, 1, 1, 1);


        assertEquals("artsiom", e.getName());
        assertEquals(19, e.getAge());
        assertEquals(1, e.getSalary());
        assertEquals(1, e.getEmployeeId());
        assertEquals(1, e.getExperience());

        e.setAge(20);
        e.setName("artsiom_1");
        e.setSalary(20);
        e.setExperience(20);
        e.setEmployeeId(20);

        assertEquals("artsiom_1", e.getName());
        assertEquals(20, e.getAge());
        assertEquals(20, e.getSalary());
        assertEquals(20, e.getEmployeeId());
        assertEquals(20, e.getExperience());
    }
}
