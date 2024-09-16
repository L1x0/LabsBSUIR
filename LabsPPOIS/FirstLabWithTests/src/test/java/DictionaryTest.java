import org.FirstLabPPOIS.Dictionary;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
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
    public void testGetWhenKeyIsNonexistent() {
        Dictionary d = new Dictionary();

        assertThrows(NoSuchElementException.class, () -> {
            d.get("1");
            throw new NoSuchElementException();
        });
    }

    @Test
    public void testConstructorWithCopy() {
        Dictionary dCopy = new Dictionary("1", "2");
        Dictionary d = new Dictionary(dCopy);
        assertEquals("2", d.get("1"));
    }

    @Test
    public void testConstructorWithKeyValue() {
        Dictionary d = new Dictionary("1", "2");
        assertEquals("2", d.get("1"));
    }


    @Test
    public void testRemove() {
        Dictionary d = new Dictionary();

        d.add("1", "2");
        d.remove("1");

        assertThrows(NoSuchElementException.class, () -> {
            d.get("1");
            throw new NoSuchElementException();
        });
    }

    @Test
    public void testGet() {
        Dictionary d = new Dictionary();
        d.add("1", "2");
        assertEquals("2", d.get("1"));
    }

    @Test
    public void testAmount() {
        Dictionary d = new Dictionary();
        d.add("1", "2");
        assertEquals(1, d.amount());
        d.add("2", "3");
        assertEquals(2, d.amount());
    }

    @Test
    public void testReplaceNotFound() {
        Dictionary d = new Dictionary();

        assertThrows(NoSuchElementException.class, () -> {
            d.replace("1", "2");
            throw new NoSuchElementException();
        });
    }

    @Test
    public void testCompare() {
        Dictionary d = new Dictionary();
        d.add("1", "2");

        assertEquals(1, d.compareTo(new Dictionary()));
    }

    @Test
    public void testGetException() {
        Dictionary d = new Dictionary();

        assertThrows(NoSuchElementException.class, () -> {
            d.get("1");
            throw new NoSuchElementException();
        });
    }

    @Test
    public void testAddException() {
        Dictionary d = new Dictionary("1", "2");

        assertThrows(IllegalStateException.class, () -> {
            d.add("1", "2");
            throw new IllegalStateException();
        });
    }

    @Test
    public void testToString() {
        Dictionary d = new Dictionary("1", "2");

        assertEquals("{1=2}", d.toString());
    }

    @Test
    public void testKeySet() {
        Dictionary d = new Dictionary();

        d.add("1", "2");
        assertEquals("[1]", d.getAllKeys());
    }
}
