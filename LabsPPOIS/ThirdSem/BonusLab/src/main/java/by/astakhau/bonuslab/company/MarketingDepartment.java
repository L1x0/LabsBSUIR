package by.astakhau.bonuslab.company;

import by.astakhau.bonuslab.data.Client;
import by.astakhau.bonuslab.data.Departments;
import by.astakhau.bonuslab.data.Employee;
import by.astakhau.bonuslab.data.Order;
import by.astakhau.bonuslab.service.ClientService;
import by.astakhau.bonuslab.service.EmployeeService;
import by.astakhau.bonuslab.service.OrderService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarketingDepartment {
    EmployeeService employeeService;
    ArrayList<Employee> employees;

    ClientService clientService;
    ArrayList<Client> clients;

    OrderService orderService;
    @Getter
    ArrayList<Order> orders;

    @Autowired
    public MarketingDepartment(EmployeeService employeeService, ClientService clientService, OrderService orderService) {
        this.employeeService = employeeService;
        this.employees = new ArrayList<>(employeeService.getEmployeesByDepartment(Departments.MarketingDepartment));

        this.clientService = clientService;
        this.clients = new ArrayList<>(clientService.getAllClients());

        this.orderService = orderService;
        this.orders = new ArrayList<>(orderService.getAllOrders());
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employeeService.saveEmployee(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employeeService.deleteEmployee(employee.getId());
    }

    public void addOrder(Order order) {
        orders.add(order);
        orderService.saveOrder(order);
    }

    public void deleteOrder(Order order) {
        orders.remove(order);
        for (Client client : clients) {
            if (client.getOrderId() == order.getId()) {
                clients.remove(client);
                break;
            }
        }

        orderService.deleteOrderById(order.getId());
        clientService.deleteByOrderId(order.getId());
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

    public Order findOrderById(long id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }
}
