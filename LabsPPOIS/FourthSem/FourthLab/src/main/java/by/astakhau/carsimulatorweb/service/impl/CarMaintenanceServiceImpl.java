package by.astakhau.carsimulatorweb.service.impl;

import by.astakhau.carsimulatorweb.service.interfaces.CarControlService;
import by.astakhau.carsimulatorweb.service.interfaces.CarMaintenanceService;
import by.astakhau.carsimulatorweb.service.interfaces.DriverService;
import by.astakhau.carsimulator.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarMaintenanceServiceImpl implements CarMaintenanceService {

    private final DriverService driverService;
    private final CarControlService carControlService;

    @Autowired
    public CarMaintenanceServiceImpl(DriverService driverService, CarControlService carControlService) {
        this.driverService = driverService;
        this.carControlService = carControlService;
    }

    @Override
    public void repairCar() {
        Car currentCar = carControlService.getCurrentCar();
        currentCar.repair();
        driverService.saveDriver(driverService.getDriver());
    }
    
    @Override
    public void refuel(int amount) {
        Car currentCar = carControlService.getCurrentCar();
        int currentFuel = currentCar.getLocalFuel().getQuantity();
        int maxFuel = currentCar.getMaxFuel().getQuantity();

        int actualAmount = Math.min(amount, maxFuel - currentFuel);
        
        if (actualAmount > 0) {
            currentCar.getLocalFuel().setQuantity((short)(currentFuel + actualAmount));
            driverService.saveDriver(driverService.getDriver());
        }
    }

    
    @Override
    public boolean checkOil() {
        return carControlService.getCurrentCar().isOilLevelOK();
    }
} 