package by.astakhau.librarymanegmentsystem;

import java.util.List;

public class Publisher {
    private String name;
    private String url;
    private List<Book> books;

    public Publisher(String name, String url, List<Book> books) {
        this.name = name;
        this.url = url;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
