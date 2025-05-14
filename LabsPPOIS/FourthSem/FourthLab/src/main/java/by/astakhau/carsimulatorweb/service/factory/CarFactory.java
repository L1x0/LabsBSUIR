package by.astakhau.carsimulatorweb.service.factory;

import by.astakhau.carsimulator.model.Car;
import org.springframework.stereotype.Component;

@Component
public class CarFactory {

    public Car createStandardCar(String name) {
        return new Car(name);
    }

    public Car createFullTankCar(String name) {
        Car car = new Car(name);

        car.getLocalFuel().setQuantity(car.getMaxFuel().getQuantity());
        return car;
    }

    public Car createSportsCar(String name) {
        Car car = new Car(name + " (Спортивный)");

        car.getEngine().setMaxEngineOilQuantity(5.0f);
        car.getMaxFuel().setQuantity((short) 70);
        car.getLocalFuel().setQuantity((short) 50);
        return car;
    }
} 