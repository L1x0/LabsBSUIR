package by.astakhau.bonuslab.company;

import by.astakhau.bonuslab.data.Departments;
import by.astakhau.bonuslab.data.Employee;
import by.astakhau.bonuslab.data.Truck;
import by.astakhau.bonuslab.service.EmployeeService;
import by.astakhau.bonuslab.service.TruckService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class TechnicalDepartment {

    private final EmployeeService employeeService;
    private final TruckService truckService;
    private final List<Employee> employees;
    private final List<Truck> trucks;

    @Autowired
    public TechnicalDepartment(EmployeeService employeeService, TruckService truckService) {
        this.employeeService = employeeService;
        this.truckService = truckService;
        this.employees = new ArrayList<>(employeeService.getEmployeesByDepartment(Departments.TechnicalDepartment));
        this.trucks = new ArrayList<>((List<Truck>) truckService.getAllTrucks());
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employeeService.saveEmployee(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employeeService.deleteEmployee(employee.getId());
    }

    public void addTruck(Truck truck) {
        trucks.add(truck);
        truckService.saveTruck(truck);
    }

    public void removeTruck(Truck truck) {
        trucks.remove(truck);
        truckService.deleteTruckById(truck.getId());
    }

    public Employee findEmployeeById(long id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    public Truck findTruckById(long id) {
        for (Truck truck : trucks) {
            if (truck.getId() == id) {
                return truck;
            }
        }
        return null;
    }
}