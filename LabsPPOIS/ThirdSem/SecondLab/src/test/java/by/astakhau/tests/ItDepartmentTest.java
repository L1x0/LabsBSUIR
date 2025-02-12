package by.astakhau.tests;

import by.astakhau.autotransportcompany.Employee;
import by.astakhau.autotransportcompany.ItDepartment;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItDepartmentTest {

    @Test
    public void getterAndSetterTest() {
        ArrayList<Employee> arr = new ArrayList<>();
        arr.add(new Employee("1", 1, 1, 1, 1));
        arr.add(new Employee("2", 2, 2, 2, 2));

        ItDepartment it = new ItDepartment(arr, 3);

        assertEquals(3, it.getCountOfComputers());
        assertEquals(2, it.getCountOfEmployees());
        assertEquals("1", it.getEmployees().get(0).getName());

        arr.add(new Employee("3", 3, 3, 3, 3));

        it.setEmployees(arr);

        assertEquals("3", it.getEmployees().get(2).getName());

        it.setCountOfComputers(5);
        assertEquals(5, it.getCountOfComputers());
    }

    @Test
    public void removeAndAddEmployeeTest() {
        ArrayList<Employee> arr = new ArrayList<>();
        arr.add(new Employee("1", 1, 1, 1, 1));
        arr.add(new Employee("2", 2, 2, 2, 2));
        ItDepartment it = new ItDepartment(arr, 3);

        it.addEmployee(new Employee("3", 3, 3, 3, 3));

        assertEquals("3", it.getEmployees().get(2).getName());

        it.removeEmployee(3);

        assertEquals("2", it.getEmployees().get(it.getEmployees().size() - 1).getName());
    }

    @Test
    void AddAndRemoveAndGetTaskTest() {
        ItDepartment it = new ItDepartment(new ArrayList<Employee>(), 3);

        it.addTask("1");
        it.addTask("2");

        assertEquals("1", it.getTasks().get(0));
        it.removeTask("1");
        assertEquals("2", it.getTasks().get(0));

        ArrayList<String> tasks = new ArrayList<>();
        tasks.add("3");
        tasks.add("4");
        it.addTasks(tasks);

        assertEquals("4", it.getTasks().get(it.getTasks().size() - 1));

    }
}
