package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class CustomerServiceDepartment extends Department {
    private ArrayList<Client> clients;
    private LogisticsDepartment logisticsDepartment;

    public void setLogisticsDepartment(LogisticsDepartment logisticsDepartment) {
        this.logisticsDepartment = logisticsDepartment;
    }

    public CustomerServiceDepartment(ArrayList<Employee> employees, ArrayList<Client> clients) {
        super(employees);
        this.clients = clients;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public void createTaskForLogistics(String name, String task) {
        clients.removeIf(client -> client.getName().equals(name));

        logisticsDepartment.addTask(task);
    }
}
