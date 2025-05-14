package by.astakhau.carsimulatorweb.service.interfaces;

public interface CarMaintenanceService {
    void repairCar();
    void refuel(int amount);
    boolean checkOil();
} 