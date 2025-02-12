package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Book;
import by.astakhau.librarymanegmentsystem.LibraryCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibraryCardTest {
    public static LibraryCard lc;

    @BeforeAll
    public static void init() {
        lc = new LibraryCard(1, List.of(new Book(1),
                new Book(2)));
    }

    @Test
    public void gettersAndSettersTest() {
        List<Book> books = List.of(new Book(1), new Book(2), new Book(3));
        lc.setBooks(books);
        assertEquals(books, lc.getBooks());

        lc.setId(2);
        assertEquals(2, lc.getId());
    }
}
