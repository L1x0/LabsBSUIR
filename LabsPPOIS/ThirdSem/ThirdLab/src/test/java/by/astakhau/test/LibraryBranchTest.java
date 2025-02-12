package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibraryBranchTest {

    @Test
    public void testConstructorWithCityAndCountry() {
        String city = "Minsk";
        String country = "Belarus";

        LibraryBranch branch = new LibraryBranch(city, country);

        assertEquals(city, branch.getCity());
        assertEquals(country, branch.getCountry());
    }

    @Test
    public void testSetCity() {
        LibraryBranch branch = new LibraryBranch("Minsk", "Belarus");
        branch.setCity("Gomel");
        assertEquals("Gomel", branch.getCity());
    }

    @Test
    public void testSetCountry() {
        LibraryBranch branch = new LibraryBranch("Minsk", "Belarus");
        branch.setCountry("Russia");
        assertEquals("Russia", branch.getCountry());
    }
}
