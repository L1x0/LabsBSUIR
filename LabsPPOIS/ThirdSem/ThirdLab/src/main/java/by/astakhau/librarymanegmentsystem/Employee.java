package by.astakhau.librarymanegmentsystem;

public class Employee extends People{
    int salary;
    long employeeId;

    public Employee(String name, String lastName, int age,
                    int height, int weight, int salary, long employeeId) {
        super(name, lastName, age, height, weight);
        this.salary = salary;
        this.employeeId = employeeId;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }
}
