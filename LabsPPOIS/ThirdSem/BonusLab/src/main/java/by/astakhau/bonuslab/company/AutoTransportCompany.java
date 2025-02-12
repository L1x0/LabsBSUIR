package by.astakhau.bonuslab.company;

import by.astakhau.bonuslab.data.Client;
import by.astakhau.bonuslab.data.Employee;
import by.astakhau.bonuslab.data.Order;
import by.astakhau.bonuslab.data.Truck;
import by.astakhau.bonuslab.service.ClientService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class AutoTransportCompany {
    FinanceDepartment financeDepartment;
    LogisticsDepartment logisticsDepartment;
    MarketingDepartment marketingDepartment;
    TechnicalDepartment technicalDepartment;
    ClientService clientService;

    @Autowired
    public AutoTransportCompany(FinanceDepartment financeDepartment, LogisticsDepartment logisticsDepartment,
                                MarketingDepartment marketingDepartment, TechnicalDepartment technicalDepartment,
                                ClientService clientService) {
        this.financeDepartment = financeDepartment;
        this.logisticsDepartment = logisticsDepartment;
        this.marketingDepartment = marketingDepartment;
        this.technicalDepartment = technicalDepartment;
        this.clientService = clientService;
    }

    public void addEmployeeForFinanceDepartment(Employee employee) {
        financeDepartment.addEmployee(employee);
    }

    public void addEmployeeForLogisticsDepartment(Employee employee) {
        logisticsDepartment.addEmployee(employee);
    }

    public void addEmployeeForMarketingDepartment(Employee employee) {
        marketingDepartment.addEmployee(employee);
    }

    public void addEmployeeForTechnicalDepartment(Employee employee) {
        technicalDepartment.addEmployee(employee);
    }

    public void addOrdersForMarketingDepartment(Order order) {
        marketingDepartment.addOrder(order);
    }

    public void addTrucksForTechnicalDepartment(Truck truck) {
        technicalDepartment.addTruck(truck);
    }

    public void removePutTruckOnTheRoad(Truck truck) {
        technicalDepartment.removeTruck(truck);
    }

    public void removeEmployeeForMarketingDepartment(Employee employee) {
        marketingDepartment.removeEmployee(employee);
    }

    public void removeEmployeeForTechnicalDepartment(Employee employee) {
        technicalDepartment.removeEmployee(employee);
    }

    public void removeEmployeeForFinanceDepartment(Employee employee) {
        financeDepartment.removeEmployee(employee);
    }

    public void removeEmployeeForLogisticsDepartment(long id) {
        logisticsDepartment.removeEmployee(id);
    }

    public void removeOrdersForMarketingDepartment(Order order) {
        marketingDepartment.deleteOrder(order);
    }

    public void addBalance(int amount) {
        financeDepartment.addBalance(amount);
    }

    public void removeBalance(int amount) {
        financeDepartment.removeBalance(amount);
    }

    public List<Employee> getEmployeesFinanceDepartment() {
        return financeDepartment.getEmployees();
    }

    public List<Employee> getEmployeesTechnicalDepartment() {
        return technicalDepartment.getEmployees();
    }

    public List<Employee> getEmployeesLogisticsDepartment() {
        return logisticsDepartment.getEmployees();
    }

    public List<Employee> getEmployeesMarketingDepartment() {
        return marketingDepartment.getEmployees();
    }

    public Employee getEmployeeByIdFinanceDepartment(long id) {
        return financeDepartment.findEmployeeById(id);
    }

    public Employee getEmployeeByIdTechnicalDepartment(long id) {
        return technicalDepartment.findEmployeeById(id);
    }

    public Employee getEmployeeByIdMarketingDepartment(long id) {
        return marketingDepartment.findEmployeeById(id);
    }

    public Employee getEmployeeByIdLogisticsDepartment(long id) {
        return logisticsDepartment.findEmployeeById(id);
    }

    public void addClient(Client client) {
        clientService.saveClient(client);
    }
}
