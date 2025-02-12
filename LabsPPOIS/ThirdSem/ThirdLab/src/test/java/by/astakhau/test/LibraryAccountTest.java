package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Book;
import by.astakhau.librarymanegmentsystem.LibraryAccount;
import by.astakhau.librarymanegmentsystem.Notification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryAccountTest {
    private static LibraryAccount la;

    @BeforeAll
    public static void setUp() {
        la = new LibraryAccount("1", List.of(new Book(1), new Book(2)),
                "123", "123");
    }

    @Test
    public void gettersAndSettersTest() {
        List<Book> books = List.of(new Book(1), new Book(2), new Book(3));
        la.setBooks(books);
        assertEquals(books, la.getBooks());

        assertEquals("1", la.getLibraryName());
    }

    @Test
    public void authenticationTest() {
        assertTrue(la.authentication("123", "123"));
    }


    @Test
    public void addNotification() {
        la.addNotifications(new Notification("1","1"));
        assertEquals(1, la.getNotificationsCount());
    }

}
