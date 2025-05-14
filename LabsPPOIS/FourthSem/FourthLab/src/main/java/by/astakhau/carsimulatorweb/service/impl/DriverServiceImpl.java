package by.astakhau.carsimulatorweb.service.impl;

import by.astakhau.carsimulatorweb.service.interfaces.DriverService;
import by.astakhau.carsimulator.cotroller.SimulatorManager;
import by.astakhau.carsimulator.model.Driver;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceImpl implements DriverService {

    private Driver driver;

    @Override
    public void initializeDriver() {
        driver = SimulatorManager.loadState();
        if (driver == null) {
            driver = new Driver();
            SimulatorManager.saveState(driver);
            driver.setCurrentCarIndex(0);
        }
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public void saveDriver(Driver driver) {
        SimulatorManager.saveState(driver);
    }
} 