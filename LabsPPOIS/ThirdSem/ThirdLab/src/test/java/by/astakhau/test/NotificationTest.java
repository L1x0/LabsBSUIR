package by.astakhau.test;

import by.astakhau.librarymanegmentsystem.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTest {
    private static Notification n;

    @BeforeAll
    public static void setUp() {
        n = new Notification("1", "2");
    }

    @Test
    public void gettersAndSettersTest() {
        n.setMessage("message");
        assertEquals("message", n.getMessage());

        n.setTitle("title");
        assertEquals("title", n.getTitle());
    }
}
