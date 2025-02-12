package by.astakhau.librarymanegmentsystem;

import java.util.List;

public class Author extends People{
    private String alias;
    List<Book> writtenBooks;

    public Author(String name, String lastName, int age, int height,
                  int weight, String alias, List<Book> writtenBooks) {
        super(name, lastName, age, height, weight);
        this.alias = alias;
        this.writtenBooks = writtenBooks;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<Book> getWrittenBooks() {
        return writtenBooks;
    }

    public void setWrittenBooks(List<Book> writtenBooks) {
        this.writtenBooks = writtenBooks;
    }
}
