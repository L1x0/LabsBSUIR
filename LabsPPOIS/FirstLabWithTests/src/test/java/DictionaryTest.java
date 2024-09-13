import org.example.Dictionary;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class DictionaryTest {
    @Test
    public void testAdd() {
        Dictionary d = new Dictionary();

        d.add("one", "two");
        assertEquals("two", d.get("one"));
    }

    @Test
    public void testAddWithEmptyStringInValue() {
        Dictionary d = new Dictionary();

        d.add("one", "");
        assertEquals("", d.get("one"));
    }

    @Test
    public void testAddWithEmptyStringInKey() {
        Dictionary d = new Dictionary();

        d.add("", "two");
        assertEquals("two", d.get(""));
    }

    @Test
    public void testAddWithEmptyKeyAndValue() {
        Dictionary d = new Dictionary();

        d.add("", "");
        assertEquals("", d.get(""));
    }

    @Test
    public void testReplace() {
        Dictionary d = new Dictionary();

        d.add("1", "2");
        d.replace("1", "2", "3");
        assertEquals("3", d.get("1"));
    }

}
