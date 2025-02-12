package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Book;
import by.astakhau.librarymanegmentsystem.Publisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PublisherTest {
    private static Publisher p;
    List<Book> books = List.of(new Book(1), new Book(2), new Book(3));

    @BeforeAll
    public static void setUp() {
        p = new Publisher("1", "2", List.of(new Book(1)));
    }

    @Test
    public void getterAndSetterTest() {
        p.setBooks(books);
        assertEquals(books, p.getBooks());

        p.setName("name");
        assertEquals("name", p.getName());

        p.setUrl("url");
        assertEquals("url", p.getUrl());
    }
}
