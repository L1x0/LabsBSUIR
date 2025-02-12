package by.astakhau.librarymanegmentsystem;

public class Librarian extends Employee {
    long memberId;

    public Librarian(String name, String lastName, int age, int height,
                     int weight, int salary, long employeeId, long memberId) {
        super(name, lastName, age, height, weight, salary, employeeId);
        this.memberId = memberId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
