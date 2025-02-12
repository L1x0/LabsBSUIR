package by.astakhau.tests;

import by.astakhau.autotransportcompany.Client;
import by.astakhau.autotransportcompany.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {

    @Test
    public void getterAndSetterTest() {
        Order a = new Order("1", 1, 1, 1);
        Order b = new Order("1", 2, 2, 2);

        Client c = new Client("artsiom", 19, a);
        assertEquals(a, c.getOrder());
        c.setOrder(b);
        assertEquals(b, c.getOrder());

        assertEquals("artsiom", c.getName());
        assertEquals(19, c.getAge());

        c.setAge(20);
        c.setName("artsiom_1");

        assertEquals("artsiom_1", c.getName());
        assertEquals(20, c.getAge());
    }
}
