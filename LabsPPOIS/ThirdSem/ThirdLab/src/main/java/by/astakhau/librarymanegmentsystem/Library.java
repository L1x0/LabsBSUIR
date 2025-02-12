package by.astakhau.librarymanegmentsystem;

import java.util.List;

public class Library {
    Author author;
     String name;
     Catalog catalog;
     Librarian librarian;
     List<LibraryCard> cards;
     List<Member> members;

    public Library(Author author, String name, Catalog catalog, Librarian librarian,
                   List<LibraryCard> cards, List<Member> members) {
        this.author = author;
        this.name = name;
        this.catalog = catalog;
        this.librarian = librarian;
        this.cards = cards;
        this.members = members;
    }

    public Library() {}

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public List<LibraryCard> getCards() {
        return cards;
    }

    public void setCards(List<LibraryCard> cards) {
        this.cards = cards;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
