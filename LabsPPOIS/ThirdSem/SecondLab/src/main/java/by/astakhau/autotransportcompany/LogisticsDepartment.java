package by.astakhau.autotransportcompany;


import java.util.ArrayList;

public class LogisticsDepartment extends Department {
    ArrayList<Truck> trucksOnBase;
    ArrayList<Truck> trucksOnTheWay;
    TechnicalDepartment technicalDepartment;

    public LogisticsDepartment(ArrayList<Employee> employees, ArrayList<Truck> trucksOnBase,
                               ArrayList<Truck> trucksOnTheWay, TechnicalDepartment technicalDepartment) {
        super(employees);
        this.trucksOnBase = trucksOnBase;
        this.trucksOnTheWay = trucksOnTheWay;
        this.technicalDepartment = technicalDepartment;
    }

    public LogisticsDepartment(ArrayList<Employee> employees, ArrayList<Truck> trucksOnBase,
                               ArrayList<Truck> trucksOnTheWay) {

        super(employees);
        this.trucksOnBase = trucksOnBase;
        this.trucksOnTheWay = trucksOnTheWay;
    }

    public ArrayList<Truck> getTrucksOnBase() {
        return trucksOnBase;
    }

    public void setTrucksOnBase(ArrayList<Truck> trucksOnBase) {
        this.trucksOnBase = trucksOnBase;
    }

    public ArrayList<Truck> getTrucksOnTheWay() {
        return trucksOnTheWay;
    }

    public void setTrucksOnTheWay(ArrayList<Truck> trucksOnTheWay) {
        this.trucksOnTheWay = trucksOnTheWay;
    }

    public void sendTrucksOnTheWay() {
        this.trucksOnTheWay.add(this.trucksOnBase.get(this.trucksOnBase.size() - 1));
        this.trucksOnBase.remove(this.trucksOnBase.size() - 1);
    }

    public void sendTrucksToBase() {
        this.trucksOnBase.add(this.trucksOnTheWay.get(this.trucksOnTheWay.size() - 1));
        this.trucksOnTheWay.remove(this.trucksOnTheWay.size() - 1);
    }

    public void sendTrucksToTechnicalDepartment(String model) {
        for (Truck truck : this.trucksOnBase) {
            if (truck.getModel().equals(model)) {
                this.trucksOnBase.remove(truck);
                technicalDepartment.addTruckToRepair(truck);
            }
        }
    }
}
