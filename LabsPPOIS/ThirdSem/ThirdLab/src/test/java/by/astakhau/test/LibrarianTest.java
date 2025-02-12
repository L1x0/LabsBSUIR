package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Librarian;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibrarianTest {
    private static Librarian l;

    @BeforeAll
    public static void setUp() {
        l = new Librarian("1", "1", 15, 150, 150,
                1500, 130, 22);
    }

    @Test
    public void gettersAndSetters() {
        l.setLastName("last name");
        assertEquals("last name", l.getLastName());

        l.setMemberId(11);
        assertEquals(11, l.getMemberId());

        l.setEmployeeId(12);
        assertEquals(12, l.getEmployeeId());

        l.setSalary(13);
        assertEquals(13, l.getSalary());
    }
}
