package by.astakhau.tests;

import by.astakhau.autotransportcompany.Employee;
import by.astakhau.autotransportcompany.LogisticsDepartment;
import by.astakhau.autotransportcompany.TechnicalDepartment;
import by.astakhau.autotransportcompany.Truck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogisticsDepartmentTest {

    @Test
    public void getterAndSetterTest() {
        ArrayList<Employee> arr = new ArrayList<>();
        arr.add(new Employee("1", 1, 1, 1, 1));
        arr.add(new Employee("2", 2, 2, 2, 2));

        ArrayList<Truck> carsOnBase = new ArrayList<>();
        carsOnBase.add(new Truck("1", "1", "1", 1,
                1, 1, 1));

        carsOnBase.add(new Truck("2", "2", "2", 2,
                2, 2, 2));

        ArrayList<Truck> carsOnTruck = new ArrayList<>();
        carsOnTruck.add(new Truck("3", "3", "3",
                3, 3, 3, 3));

        carsOnTruck.add(new Truck("4", "4", "4",
                4, 4, 4, 4));

        LogisticsDepartment ld = new LogisticsDepartment(arr, new ArrayList<>(), new ArrayList<>());

        ld.setTrucksOnTheWay(carsOnTruck);
        ld.setTrucksOnBase(carsOnBase);

        assertEquals(arr.size(), ld.getEmployees().size());
        assertEquals(carsOnBase, ld.getTrucksOnBase());
        assertEquals(carsOnTruck, ld.getTrucksOnTheWay());
    }

    @Test
    void sendToTheWayAndMoreTest() {
        ArrayList<Truck> carsOnBase = new ArrayList<>();
        carsOnBase.add(new Truck("1", "1", "1", 1,
                1, 1, 1));

        carsOnBase.add(new Truck("2", "2", "2", 2,
                2, 2, 2));

        ArrayList<Truck> carsOnTruck = new ArrayList<>();
        carsOnTruck.add(new Truck("3", "3", "3",
                3, 3, 3, 3));

        carsOnTruck.add(new Truck("4", "4", "4",
                4, 4, 4, 4));

        LogisticsDepartment ld = new LogisticsDepartment(new ArrayList<>(), carsOnBase, carsOnTruck);

        carsOnTruck.add(0, carsOnBase.get(carsOnBase.size() - 1));
        carsOnBase.remove(carsOnBase.size() - 1);

        ld.sendTrucksOnTheWay();

        assertEquals(carsOnTruck, ld.getTrucksOnTheWay());
        assertEquals(carsOnBase, ld.getTrucksOnBase());

        ld.sendTrucksToBase();

        carsOnBase.add(0, carsOnTruck.get(carsOnTruck.size() - 1));
        carsOnTruck.remove(carsOnTruck.size() - 1);

        assertEquals(carsOnTruck, ld.getTrucksOnTheWay());
        assertEquals(carsOnBase, ld.getTrucksOnBase());
    }

    @Test
    void sendTrucksToTechnicalDepartmentTest() {
        ArrayList<Truck> carsOnBase = new ArrayList<>();
        carsOnBase.add(new Truck("1", "1", "1", 1,
                1, 1, 1));

        carsOnBase.add(new Truck("2", "2", "2", 2,
                2, 2, 2));

        LogisticsDepartment ld = new LogisticsDepartment(new ArrayList<>(), carsOnBase
                , new ArrayList<>(), new TechnicalDepartment(new ArrayList<>(), new ArrayList<>()));

        ld.sendTrucksToTechnicalDepartment("1");

        assertEquals(1, ld.getTrucksOnBase().size());
    }
}
