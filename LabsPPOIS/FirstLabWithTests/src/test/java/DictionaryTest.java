import org.example.Dictionary;
import org.junit.jupiter.api.Test;

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
        d.replace("1", "3");
        assertEquals("3", d.get("1"));
    }

    @Test
    public void testFileInput() {
        Dictionary d = new Dictionary();

        d.addFromFile("input.txt");
        assertEquals("2", d.get("1"));
        assertEquals("4", d.get("3"));
    }

    @Test
    public void testGetWhenKeyIsNonexistent() {
        Dictionary d = new Dictionary();

        assertNull(d.get("1"));
    }

    @Test
    public void testGet() {
        Dictionary d = new Dictionary();
        d.add("1", "2");
        assertEquals("2", d.get("1"));
    }
}
