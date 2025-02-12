package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Author;
import by.astakhau.librarymanegmentsystem.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class BookTest {
    private static Book book;
    private static Author a;

    @BeforeAll
    public static void setup() {
        a = new Author("0", "2", 3, 4, 5, "6",
                List.of(new Book(1), new Book(2), new Book(3)));
        book = new Book("1", a, "2",3, 4);
    }

    @Test
    public void gettersAndSetters() {
        a = new Author("0", "2", 3, 4, 5, "6",
                List.of(new Book(1), new Book(2), new Book(3)));
        book.setAuthor(a);
        assertEquals(a, book.getAuthor());

        book.setLanguage("rus");
        assertEquals("rus", book.getLanguage());

        book.setYear(2005);
        assertEquals(2005, book.getYear());

        book.setTitle("title");
        assertEquals("title", book.getTitle());

        book.setLibraryID(12);
        assertEquals(12, book.getLibraryID());
    }
}
