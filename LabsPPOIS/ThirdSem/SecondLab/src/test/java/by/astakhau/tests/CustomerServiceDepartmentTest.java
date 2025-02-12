package by.astakhau.tests;

import by.astakhau.autotransportcompany.Client;
import by.astakhau.autotransportcompany.CustomerServiceDepartment;
import by.astakhau.autotransportcompany.LogisticsDepartment;
import by.astakhau.autotransportcompany.Order;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerServiceDepartmentTest {
    @Test
    public void getterAndSetterTest() {
        CustomerServiceDepartment cs = new CustomerServiceDepartment(new ArrayList<>(), new ArrayList<>());

        cs.setClients(new ArrayList<>(List.of(new Client("2", 2,
                new Order("2", 2, 2, 2)))));
        assertEquals(2, cs.getClients().get(0).getAge());
    }

    @Test
    void createTaskForLogisticsTest() {
        CustomerServiceDepartment cs = new CustomerServiceDepartment(new ArrayList<>(), new ArrayList<>());

        LogisticsDepartment ld = new LogisticsDepartment(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        cs.addClient(new Client("1", 1,
                new Order("1", 1, 1, 1)));
        
        cs.addClient(new Client("2", 2,
                new Order("2", 2, 2, 2)));
        cs.setLogisticsDepartment(ld);
        cs.createTaskForLogistics("2", "bimbimbombom");
        
        assertEquals("bimbimbombom", ld.getTasks().get(0));
        assertEquals("1", cs.getClients().get(0).getName());
    }
}
