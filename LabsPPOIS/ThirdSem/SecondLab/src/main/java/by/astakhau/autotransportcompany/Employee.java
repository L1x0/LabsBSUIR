package by.astakhau.autotransportcompany;

public class Employee extends People {
    private int salary;
    private int experience;
    private int employeeId;

    public Employee(String name, int age, int salary, int experience, int employeeId) {
        super(name, age);
        this.salary = salary;
        this.experience = experience;
        this.employeeId = employeeId;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void addSalary(int allowance) {
        this.salary += allowance;
    }
}
