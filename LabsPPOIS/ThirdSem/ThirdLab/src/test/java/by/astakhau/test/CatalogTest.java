package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Catalog;
import by.astakhau.librarymanegmentsystem.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatalogTest {
    private static Catalog c;

    @BeforeAll
    public static void setUp() {
        c = new Catalog("1", List.of(new Category()));
    }

    @Test
    public void getterAndSettersTest() {
        c.setCategories(List.of(new Category(), new Category()));
        assertEquals(2, c.getCategories().size());

        c.setName("name");
        assertEquals("name", c.getName());
    }
}
