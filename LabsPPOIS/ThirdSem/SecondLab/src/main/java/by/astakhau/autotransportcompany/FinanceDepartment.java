package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class FinanceDepartment extends Department {
    private int budget;

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public FinanceDepartment(ArrayList<Employee> employees, int budget) {
        super(employees);
        this.budget = budget;
    }

    public void addMoneyToBudget(int amount) {
        budget += amount;
    }
}
