package by.astakhau.librarymanegmentsystem;

public class Book {
    private String title;
    private Author author;
    private String language;
    private int year;
    private int libraryID;

    public Book(String title, Author author, String language, int year, int libraryID) {
        this.title = title;
        this.author = author;
        this.language = language;
        this.year = year;
        this.libraryID = libraryID;
    }

    public Book(int libraryID) {
        this.libraryID = libraryID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getLibraryID() {
        return libraryID;
    }

    public void setLibraryID(int libraryID) {
        this.libraryID = libraryID;
    }
}
