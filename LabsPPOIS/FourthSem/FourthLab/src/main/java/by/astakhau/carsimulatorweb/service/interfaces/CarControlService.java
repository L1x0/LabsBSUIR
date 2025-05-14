package by.astakhau.carsimulatorweb.service.interfaces;

import by.astakhau.carsimulator.model.Car;

public interface CarControlService {

    Car getCurrentCar();
    void startEngine();
    void stopEngine();
    void moveForward();
    void moveBackward();
    void stopCar();
    void turnLeft();
    void turnRight();
    void turnStraight();
} 