package by.astakhau.bonuslab.web;

import by.astakhau.bonuslab.company.AutoTransportCompany;
import by.astakhau.bonuslab.data.Client;
import by.astakhau.bonuslab.data.Employee;
import by.astakhau.bonuslab.data.Order;
import by.astakhau.bonuslab.data.Truck;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class AutoTransportCompanyController {
    private final AutoTransportCompany autoTransportCompany;

    @Autowired
    public AutoTransportCompanyController(AutoTransportCompany autoTransportCompany) {
        this.autoTransportCompany = autoTransportCompany;
    }

    @PostMapping("/finance/employees")
    public ResponseEntity<Employee> addFinanceEmployee(@Valid @RequestBody Employee employee) {
        autoTransportCompany.addEmployeeForFinanceDepartment(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @GetMapping("/finance/employees")
    public ResponseEntity<List<Employee>> getFinanceEmployees() {
        List<Employee> employees = autoTransportCompany.getEmployeesFinanceDepartment();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @DeleteMapping("/finance/employees/{id}")
    public ResponseEntity<Void> deleteFinanceEmployee(@PathVariable Long id) {
        Employee employee = autoTransportCompany.getFinanceDepartment().findEmployeeById(id);
        if (employee != null) {
            autoTransportCompany.removeEmployeeForFinanceDepartment(employee);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/logistics/employees")
    public ResponseEntity<Employee> addLogisticsEmployee(@Valid @RequestBody Employee employee) {
        autoTransportCompany.addEmployeeForLogisticsDepartment(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @GetMapping("/logistics/employees")
    public ResponseEntity<List<Employee>> getLogisticsEmployees() {
        List<Employee> employees = autoTransportCompany.getEmployeesLogisticsDepartment();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @DeleteMapping("/logistics/employees/{id}")
    public ResponseEntity<Void> deleteLogisticsEmployee(@PathVariable Long id) {
        Employee employee = autoTransportCompany.getEmployeeByIdLogisticsDepartment(id);
        if (employee != null) {
            autoTransportCompany.removeEmployeeForLogisticsDepartment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/marketing/employees")
    public ResponseEntity<Employee> addMarketingEmployee(@Valid @RequestBody Employee employee) {
        autoTransportCompany.addEmployeeForMarketingDepartment(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @GetMapping("/marketing/employees")
    public ResponseEntity<List<Employee>> getMarketingEmployees() {
        List<Employee> employees = autoTransportCompany.getEmployeesMarketingDepartment();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @DeleteMapping("/marketing/employees/{id}")
    public ResponseEntity<Void> deleteMarketingEmployee(@PathVariable Long id) {
        Employee employee = autoTransportCompany.getEmployeeByIdMarketingDepartment(id);
        if (employee != null) {
            autoTransportCompany.removeEmployeeForMarketingDepartment(employee);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/technical/employees")
    public ResponseEntity<Employee> addTechnicalEmployee(@Valid @RequestBody Employee employee) {
        autoTransportCompany.addEmployeeForTechnicalDepartment(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @GetMapping("/technical/employees")
    public ResponseEntity<List<Employee>> getTechnicalEmployees() {
        List<Employee> employees = autoTransportCompany.getEmployeesTechnicalDepartment();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @DeleteMapping("/technical/employees/{id}")
    public ResponseEntity<Void> deleteTechnicalEmployee(@PathVariable Long id) {
        Employee employee = autoTransportCompany.getEmployeeByIdTechnicalDepartment(id);
        if (employee != null) {
            autoTransportCompany.removeEmployeeForTechnicalDepartment(employee);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/marketing/orders")
    public ResponseEntity<Order> addMarketingOrder(@Valid @RequestBody Order order) {
        autoTransportCompany.addOrdersForMarketingDepartment(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/marketing/client")
    public ResponseEntity<Client> addMarketingOrder(@Valid @RequestBody Client client) {
        autoTransportCompany.addClient(client);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @GetMapping("/marketing/orders")
    public ResponseEntity<List<Order>> getMarketingOrders() {
        List<Order> orders = autoTransportCompany.getMarketingDepartment().getOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @DeleteMapping("/marketing/orders/{id}")
    public ResponseEntity<Void> deleteMarketingOrder(@PathVariable Long id) {
        Order order = autoTransportCompany.getMarketingDepartment().findOrderById(id);
        if (order != null) {
            autoTransportCompany.removeOrdersForMarketingDepartment(order);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/technical/trucks")
    public ResponseEntity<Truck> addTechnicalTruck(@Valid @RequestBody Truck truck) {
        autoTransportCompany.addTrucksForTechnicalDepartment(truck);
        return new ResponseEntity<>(truck, HttpStatus.CREATED);
    }

    @GetMapping("/technical/trucks")
    public ResponseEntity<List<Truck>> getTechnicalTrucks() {
        List<Truck> trucks = autoTransportCompany.getTechnicalDepartment().getTrucks();
        return new ResponseEntity<>(trucks, HttpStatus.OK);
    }

    @DeleteMapping("/technical/trucks/{id}")
    public ResponseEntity<Void> deleteTechnicalTruck(@PathVariable Long id) {
        Truck truck = autoTransportCompany.getTechnicalDepartment().findTruckById(id);
        if (truck != null) {
            autoTransportCompany.removePutTruckOnTheRoad(truck);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/finance/balance/add")
    public ResponseEntity<Void> addBalance(@RequestParam int amount) {
        autoTransportCompany.addBalance(amount);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/finance/balance/remove")
    public ResponseEntity<Void> removeBalance(@RequestParam int amount) {
        autoTransportCompany.removeBalance(amount);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
