package by.astakhau.tests;

import by.astakhau.autotransportcompany.Truck;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrackTest {

    @Test
    public void getterAndSetterTest() {
        Truck t = new Truck("1", "1", "1", 1, 1, 1, 1);
        
        assertEquals("1", t.getColor());
        assertEquals("1", t.getEngine());
        assertEquals("1", t.getModel());
        assertEquals(1, t.getMaxWeight());
        assertEquals(1, t.getMaxSpeed());
        assertEquals(1, t.getWeight());
        assertEquals(1, t.getNumberOfAxes());
        
        t.setColor("2");
        t.setEngine("2");
        t.setModel("2");
        t.setWeight(2);
        t.setNumberOfAxes(2);
        t.setMaxWeight(2);
        t.setMaxSpeed(2);

        assertEquals("2", t.getColor());
        assertEquals("2", t.getEngine());
        assertEquals("2", t.getModel());
        assertEquals(2, t.getMaxWeight());
        assertEquals(2, t.getMaxSpeed());
        assertEquals(2, t.getWeight());
        assertEquals(2, t.getNumberOfAxes());

        LinkedList<String> list = new LinkedList<>();
        list.add("1");
        list.add("2");

        t.setRoute(list);

        assertEquals(list, t.getRoute());

        t.addCityToRoute("3");
        list.add("3");

        assertEquals(list, t.getRoute());
    }
}