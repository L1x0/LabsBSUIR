package by.astakhau.librarymanegmentsystem;

import java.util.ArrayList;
import java.util.List;

public final class LibraryAccount {
    private String libraryName;
    private List<Book> books;
    private String accountLogin;
    private String accountPassword;
    private ArrayList<Notification> notifications = new ArrayList<>();

    public LibraryAccount(String libraryName, List<Book> books, String accountLogin, String accountPassword) {
        this.libraryName = libraryName;
        this.books = books;
        this.accountLogin = accountLogin;
        this.accountPassword = accountPassword;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public boolean authentication(String accountLogin, String accountPassword) {
        return accountLogin.equals(this.accountLogin) && accountPassword.equals(this.accountPassword);
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void addNotifications(Notification notification) {
        this.notifications.add(notification);
    }

    public int getNotificationsCount() {
        return this.notifications != null ? this.notifications.size() : 0;
    }
}
