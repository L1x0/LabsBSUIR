package by.astakhau.tests;

import by.astakhau.autotransportcompany.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    @Test
    public void getterAndSetterTest() {
        Order p = new Order("1", 2, 3 ,4);

        assertEquals("1", p.getCountry());
        assertEquals(2, p.getProductId());
        assertEquals(3, p.getCustomerId());
        assertEquals(4, p.getId());

        p.setCountry("2");
        p.setProductId(5);
        p.setCustomerId(6);
        p.setId(7);

        assertEquals("2", p.getCountry());
        assertEquals(5, p.getProductId());
        assertEquals(6, p.getCustomerId());
        assertEquals(7, p.getId());
    }
}
