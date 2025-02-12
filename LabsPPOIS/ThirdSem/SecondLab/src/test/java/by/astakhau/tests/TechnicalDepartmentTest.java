package by.astakhau.tests;

import by.astakhau.autotransportcompany.TechnicalDepartment;
import by.astakhau.autotransportcompany.Truck;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;

public class TechnicalDepartmentTest {
    @Test
    void GetterAndSetterTest() {
        TechnicalDepartment td = new TechnicalDepartment(new ArrayList<>(), new ArrayList<>());
        ArrayList<Truck> trucks = new ArrayList<>();
        trucks.add(new Truck("1", "1", "1", 1,
                1, 1, 1));

        td.setTrucksOnRepair(trucks);

        assertEquals("1", trucks.get(0).getModel());
        assertEquals("1", td.getTrucksOnRepair().get(0).getModel());
    }
}
