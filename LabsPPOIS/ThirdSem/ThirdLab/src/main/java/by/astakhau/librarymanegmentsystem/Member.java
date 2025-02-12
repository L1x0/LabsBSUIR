package by.astakhau.librarymanegmentsystem;


import java.util.ArrayList;
import java.util.List;

public class Member extends People {
    long memberId;
    List<Book> takenBooks;
    LibraryAccount libraryAccount;
    LibraryCard libraryCard;

    public Member(String name, String lastName, int age, int height,
                  int weight, long memberId, List<Book> takenBooks,
                  LibraryAccount libraryAccount, LibraryCard libraryCard) {
        super(name, lastName, age, height, weight);
        this.memberId = memberId;
        this.takenBooks = takenBooks;
        this.libraryAccount = libraryAccount;
        this.libraryCard = libraryCard;
    }

    public LibraryCard getLibraryCard() {
        return libraryCard;
    }

    public void setLibraryCard(LibraryCard libraryCard) {
        this.libraryCard = libraryCard;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public List<Book> getTakenBooks() {
        return takenBooks;
    }

    public void setTakenBooks(List<Book> takenBooks) {
        this.takenBooks = takenBooks;
    }

    public LibraryAccount getLibraryAccount() {
        return libraryAccount;
    }

    public void setLibraryAccount(LibraryAccount libraryAccount) {
        this.libraryAccount = libraryAccount;
    }

    public void addBook(int id) {
        ArrayList<Book> books = new ArrayList<>(this.takenBooks);
        books.add(new Book(id));

        this.takenBooks = books;
        this.libraryAccount.setBooks(books);
    }

    public void removeBook(int id) {
        ArrayList<Book> books = new ArrayList<>(this.takenBooks);

        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getLibraryID() == id) {
                books.remove(i);
                break;
            }
        }

        this.takenBooks = books;
        this.libraryAccount.setBooks(books);
    }

    public Member(String name, String lastName, int age, int height, int weight) {
        super(name, lastName, age, height, weight);
    }
}
