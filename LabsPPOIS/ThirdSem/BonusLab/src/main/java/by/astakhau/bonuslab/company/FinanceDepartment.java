package by.astakhau.bonuslab.company;

import by.astakhau.bonuslab.data.Departments;
import by.astakhau.bonuslab.data.Employee;
import by.astakhau.bonuslab.service.EmployeeService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FinanceDepartment {
    @Getter
    @Setter
    long balance;
    private final EmployeeService employeeService;
    @Getter
    private final List<Employee> employees;

    public FinanceDepartment(EmployeeService employeeService) {
        balance = 0;
        this.employeeService = employeeService;
        this.employees = new ArrayList<>(employeeService.getEmployeesByDepartment(Departments.FinanceDepartment));
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employeeService.saveEmployee(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employeeService.deleteEmployee(employee.getId());
    }

    public void addBalance(long amount) {
        balance += amount;
    }

    public void removeBalance(long amount) {
        balance -= amount;
    }

    public Employee findEmployeeById(long id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

}
