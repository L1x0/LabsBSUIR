package by.astakhau.carsimulatorweb.service.impl;

import by.astakhau.carsimulatorweb.service.interfaces.CarControlService;
import by.astakhau.carsimulatorweb.service.interfaces.DriverService;
import by.astakhau.carsimulator.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarControlServiceImpl implements CarControlService {

    private final DriverService driverService;

    @Autowired
    public CarControlServiceImpl(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public Car getCurrentCar() {
        return driverService.getDriver().getCar(driverService.getDriver().getCurrentCarIndex());
    }

    @Override
    public void startEngine() {
        Car currentCar = getCurrentCar();
        if (!currentCar.isRunning()) {
            currentCar.startCar();
            driverService.saveDriver(driverService.getDriver());
        }
    }

    @Override
    public void stopEngine() {
        Car currentCar = getCurrentCar();
        if (currentCar.isRunning()) {
            currentCar.stopEngine();
            driverService.saveDriver(driverService.getDriver());
        }
    }

    @Override
    public void moveForward() {
        Car currentCar = getCurrentCar();
        currentCar.rideForward();
        driverService.saveDriver(driverService.getDriver());
    }

    @Override
    public void moveBackward() {
        Car currentCar = getCurrentCar();
        currentCar.rideBackward();
        driverService.saveDriver(driverService.getDriver());
    }

    @Override
    public void stopCar() {
        Car currentCar = getCurrentCar();
        currentCar.stop();
        driverService.saveDriver(driverService.getDriver());
    }

    @Override
    public void turnLeft() {
        Car currentCar = getCurrentCar();
        currentCar.turnLeft();
        driverService.saveDriver(driverService.getDriver());
    }

    @Override
    public void turnRight() {
        Car currentCar = getCurrentCar();
        currentCar.turnRight();
        driverService.saveDriver(driverService.getDriver());
    }

    @Override
    public void turnStraight() {
        Car currentCar = getCurrentCar();
        currentCar.turnRudderStraight();
        driverService.saveDriver(driverService.getDriver());
    }
} 