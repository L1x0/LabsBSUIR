package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Author;
import by.astakhau.librarymanegmentsystem.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorTest {

    public static Author a;

    @BeforeAll
    public static void setup() {
        a = new Author("1", "2", 3, 4, 5, "6",
                List.of(new Book(1), new Book(2), new Book(3)));
    }

    @Test
    public void gettersAndSettersTest() {
        a.setName("name");
        assertEquals("name", a.getName());

        a.setAlias("alias");
        assertEquals("alias", a.getAlias());

        a.setWrittenBooks(List.of(new Book(5), new Book(6), new Book(7)));
        assertEquals(3, a.getWrittenBooks().size());

        a.setAge(7);
        assertEquals(7, a.getAge());

        a.setHeight(170);
        assertEquals(170, a.getHeight());

        a.setWeight(70);
        assertEquals(70, a.getWeight());
    }
}
