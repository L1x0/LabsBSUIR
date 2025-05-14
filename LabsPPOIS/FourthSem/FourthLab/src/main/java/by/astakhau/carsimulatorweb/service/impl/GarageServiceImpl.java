package by.astakhau.carsimulatorweb.service.impl;

import by.astakhau.carsimulatorweb.service.factory.CarFactory;
import by.astakhau.carsimulatorweb.service.interfaces.DriverService;
import by.astakhau.carsimulatorweb.service.interfaces.GarageService;
import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GarageServiceImpl implements GarageService {

    private final DriverService driverService;
    private final CarFactory carFactory;

    @Autowired
    public GarageServiceImpl(DriverService driverService, CarFactory carFactory) {
        this.driverService = driverService;
        this.carFactory = carFactory;
    }

    @Override
    public List<Map<String, Object>> getCarsList() {
        Driver driver = driverService.getDriver();
        List<Map<String, Object>> carsList = new ArrayList<>();
        
        for (int i = 0; i < driver.getCars().size(); i++) {
            Car car = driver.getCar(i);
            Map<String, Object> carInfo = new HashMap<>();
            carInfo.put("id", i);
            carInfo.put("name", car.getName());
            carInfo.put("isCurrent", i == driver.getCurrentCarIndex());
            carInfo.put("isRunning", car.isRunning());
            carInfo.put("fuelLevel", car.getLocalFuel().getQuantity());
            carInfo.put("maxFuel", car.getMaxFuel().getQuantity());
            carInfo.put("oilLevel", car.getEngine().getEngineOilQuantity());
            carInfo.put("maxOilLevel", car.getEngine().getMaxEngineOilQuantity());
            carInfo.put("needsRepair", car.getEngine().isBreading());
            
            carsList.add(carInfo);
        }
        
        return carsList;
    }

    @Override
    public void addCar(String name) {
        Driver driver = driverService.getDriver();

        Car newCar = carFactory.createStandardCar(name);
        
        driver.addCar(newCar);
        driverService.saveDriver(driver);
    }

    public void addSportsCar(String name) {
        Driver driver = driverService.getDriver();

        Car newCar = carFactory.createSportsCar(name);
        
        driver.addCar(newCar);
        driverService.saveDriver(driver);
    }

    public void addFullTankCar(String name) {
        Driver driver = driverService.getDriver();

        Car newCar = carFactory.createFullTankCar(name);
        
        driver.addCar(newCar);
        driverService.saveDriver(driver);
    }

    @Override
    public boolean removeCar(int carIndex) {
        Driver driver = driverService.getDriver();
        
        if (driver.getCars().size() <= 1) {
            return false;
        }
        
        if (carIndex == driver.getCurrentCarIndex()) {
            return false;
        }
        
        driver.removeCar(carIndex);

        if (driver.getCurrentCarIndex() > carIndex) {
            driver.setCurrentCarIndex(driver.getCurrentCarIndex() - 1);
        }
        
        driverService.saveDriver(driver);
        return true;
    }

    @Override
    public boolean switchCar(int carIndex) {
        Driver driver = driverService.getDriver();
        
        if (carIndex < 0 || carIndex >= driver.getCars().size()) {
            return false;
        }
        
        if (carIndex == driver.getCurrentCarIndex()) {
            return false;
        }
        
        driver.setCurrentCarIndex(carIndex);
        driverService.saveDriver(driver);
        return true;
    }
} 