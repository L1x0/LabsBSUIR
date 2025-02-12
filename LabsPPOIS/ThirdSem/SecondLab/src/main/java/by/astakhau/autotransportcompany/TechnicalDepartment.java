package by.astakhau.autotransportcompany;

import java.util.ArrayList;

public class TechnicalDepartment extends Department {
    private ArrayList<Truck> trucksOnRepair;

    public TechnicalDepartment(ArrayList<Employee> employees, ArrayList<Truck> trucksOnRepair) {
        super(employees);
        this.trucksOnRepair = trucksOnRepair;
    }

    public ArrayList<Truck> getTrucksOnRepair() {
        return trucksOnRepair;
    }

    public void setTrucksOnRepair(ArrayList<Truck> trucksOnRepair) {
        this.trucksOnRepair = trucksOnRepair;
    }

    public void addTruckToRepair(Truck truck) {
        this.trucksOnRepair.add(truck);
    }
}
