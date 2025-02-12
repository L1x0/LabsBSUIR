package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class ItDepartment extends Department {
    private int countOfComputers;

    public ItDepartment(ArrayList<Employee> employees, int countOfComputers) {
        super(employees);
        this.countOfComputers = countOfComputers;
    }

    public int getCountOfComputers() {
        return countOfComputers;
    }

    public void setCountOfComputers(int countOfComputers) {
        this.countOfComputers = countOfComputers;
    }
}
