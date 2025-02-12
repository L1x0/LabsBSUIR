package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Book;
import by.astakhau.librarymanegmentsystem.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryTest {
    private static Category c;
    static List<Book> books = List.of(new Book(1), new Book(2));

    @BeforeAll
    public static void setUp() {
        c = new Category("1", "2", List.of(new Book(1)));
    }

    @Test
    public void gettersAndSettersTest() {
        c.setName("name");
        assertEquals("name", c.getName());

        c.setBooks(books);
        assertEquals(books, c.getBooks());

        c.setDescription("description");
        assertEquals("description", c.getDescription());
    }
}
