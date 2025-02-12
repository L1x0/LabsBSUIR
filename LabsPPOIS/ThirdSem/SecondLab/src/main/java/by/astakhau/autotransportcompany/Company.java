package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class Company {
    FinanceDepartment financeDepartment;
    CustomerServiceDepartment customerServiceDepartment;
    ItDepartment itDepartment;
    LegalDepartment legalDepartment;
    LogisticsDepartment logisticsDepartment;
    MarketingDepartment marketingDepartment;
    SecurityDepartment securityDepartment;
    TechnicalDepartment technicalDepartment;

    public Company() {
        financeDepartment = new FinanceDepartment(new ArrayList<>(), 1000);
        customerServiceDepartment = new CustomerServiceDepartment(new ArrayList<>(), new ArrayList<>());
        itDepartment = new ItDepartment(new ArrayList<>(), 50);
        legalDepartment = new LegalDepartment(new ArrayList<>(), new ArrayList<>());
        technicalDepartment = new TechnicalDepartment(new ArrayList<>(), new ArrayList<>());
        logisticsDepartment = new LogisticsDepartment(new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), technicalDepartment);
        marketingDepartment = new MarketingDepartment(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        securityDepartment = new SecurityDepartment(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        marketingDepartment.setFinanceDepartment(financeDepartment);
        customerServiceDepartment.setLogisticsDepartment(logisticsDepartment);
    }

    public void transferMoneyToFinanceDepartment(int count) {
        marketingDepartment.sendMoneyToFinanceDepartment(count);
    }

    public void transferTruckToTechnicalDepartment(String model) {
        logisticsDepartment.sendTrucksToTechnicalDepartment(model);
    }

    public void CreateTask(String name, String task) {
        customerServiceDepartment.createTaskForLogistics(name, task);
    }
}
