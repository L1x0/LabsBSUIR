package by.astakhau.bonuslab.company;

import by.astakhau.bonuslab.data.Departments;
import by.astakhau.bonuslab.data.Employee;
import by.astakhau.bonuslab.data.Truck;
import by.astakhau.bonuslab.service.EmployeeService;
import by.astakhau.bonuslab.service.TruckService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogisticsDepartment {
    ArrayList<Employee> employees;

    private final EmployeeService employeeService;

    public LogisticsDepartment(EmployeeService employeeService) {
        this.employeeService = employeeService;
        employees = new ArrayList<>(employeeService.getEmployeesByDepartment(Departments.LogisticsDepartment));
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employeeService.saveEmployee(employee);
    }

    public void removeEmployee(long id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                employees.remove(employee);
                break;
            }
        }

        employeeService.deleteEmployee(id);
    }

    public Employee findEmployeeById(long id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
