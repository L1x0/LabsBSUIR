package by.astakhau.carsimulatorweb.service.interfaces;

import by.astakhau.carsimulator.model.Driver;

public interface DriverService {
    void initializeDriver();
    Driver getDriver();
    void saveDriver(Driver driver);
} 