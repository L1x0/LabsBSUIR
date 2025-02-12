package by.astakhau.librarymanegmentsystem;

import java.util.List;

public class LibraryCard {
    private int id;
    private List<Book> books;

    public LibraryCard(int id, List<Book> books) {
        this.id = id;
        this.books = books;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
