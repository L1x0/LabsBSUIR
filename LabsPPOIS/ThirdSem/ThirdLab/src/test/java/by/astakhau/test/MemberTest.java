package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Book;
import by.astakhau.librarymanegmentsystem.LibraryAccount;
import by.astakhau.librarymanegmentsystem.LibraryCard;
import by.astakhau.librarymanegmentsystem.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberTest {
    public static Member m;


    @BeforeAll
    public static void setUp() {
        m = new Member("1", "1", 1, 1, 1);
    }

    @Test
    public void gettersAndSettersTest() {
        m.setMemberId(1);
        assertEquals(1, m.getMemberId());

        List<Book> books = List.of(new Book(1), new Book(2), new Book(3));
        LibraryAccount la = new LibraryAccount("1", books, "1", "1");
        m.setLibraryAccount(la);
        assertEquals(la, m.getLibraryAccount());

        m.setTakenBooks(books);
        assertEquals(books, m.getTakenBooks());

        LibraryCard lc = new LibraryCard(1, books);
        m.setLibraryCard(lc);
        assertEquals(lc, m.getLibraryCard());
    }

    @Test
    public void addBookTest() {
        List<Book> books = List.of(new Book(1), new Book(2), new Book(3));
        LibraryAccount la = new LibraryAccount("1", books, "1", "1");

        m.setLibraryAccount(la);
        m.setTakenBooks(books);

        m.addBook(2);
        assertEquals(4, m.getTakenBooks().size());
        assertEquals(4, m.getLibraryAccount().getBooks().size());
    }

    @Test
    public void removeBookTest() {
        List<Book> books = List.of(new Book(1), new Book(2), new Book(3));
        LibraryAccount la = new LibraryAccount("1", books, "1", "1");

        m.setLibraryAccount(la);
        m.setTakenBooks(books);

        m.removeBook(2);
        assertEquals(2, m.getTakenBooks().size());
        assertEquals(2, m.getLibraryAccount().getBooks().size());
    }
}
