package by.astakhau.carsimulator;

import by.astakhau.carsimulator.cotroller.StateManager;
import by.astakhau.carsimulator.model.*;
import by.astakhau.carsimulator.model.fuel.Fuel;

public class Main {
    public static void main(String[] args) {
        Driver driver = StateManager.loadState();

        if (driver == null) {
            Car car = new Car(new Brakes(), new Engine(), new LightIndicators(),
                    new Transmission(1,6), new Fuel());
            driver = new Driver("1", "1", 1, true, car);

            StateManager.saveState(driver);
        }

        System.out.println(driver.toString());

        driver.setAge(20);

        StateManager.saveState(driver);
    }
}