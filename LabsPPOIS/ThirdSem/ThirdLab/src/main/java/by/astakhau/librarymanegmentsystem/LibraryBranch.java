package by.astakhau.librarymanegmentsystem;

import java.util.List;

public class LibraryBranch extends Library {
    String city;
    String country;

    public LibraryBranch(Author author, String name, Catalog catalog, Librarian librarian,
                         List<LibraryCard> cards, List<Member> members, String city, String country) {
        super(author, name, catalog, librarian, cards, members);
        this.city = city;
        this.country = country;
    }

    public LibraryBranch(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
