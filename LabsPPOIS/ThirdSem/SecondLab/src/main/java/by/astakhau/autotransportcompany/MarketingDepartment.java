package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class MarketingDepartment extends Department {
    private ArrayList<String> promotions;
    private ArrayList<String> areasOfAnalysis;
    private FinanceDepartment financeDepartment;

    public MarketingDepartment(ArrayList<Employee> employees, ArrayList<String> promotions,
                               ArrayList<String> areasOfAnalysis) {
        super(employees);
        this.promotions = promotions;
        this.areasOfAnalysis = areasOfAnalysis;
    }

    public ArrayList<String> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<String> promotions) {
        this.promotions = promotions;
    }

    public ArrayList<String> getAreasOfAnalysis() {
        return areasOfAnalysis;
    }

    public void setAreasOfAnalysis(ArrayList<String> areasOfAnalysis) {
        this.areasOfAnalysis = areasOfAnalysis;
    }

    public void setFinanceDepartment(FinanceDepartment financeDepartment) {
        this.financeDepartment = financeDepartment;
    }

    public void sendMoneyToFinanceDepartment(int money) {
        financeDepartment.addMoneyToBudget(money);
    }
}
