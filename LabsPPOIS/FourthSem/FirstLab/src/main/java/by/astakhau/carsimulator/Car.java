package by.astakhau.carsimulator;

import by.astakhau.carsimulator.fuel.Fuel;
import by.astakhau.carsimulator.fuel.FuelTypes;
import by.astakhau.carsimulator.fuel.GasStation;

public class Car {
    Brakes brakes;
    Engine engine;
    LightIndicators lightIndicator;
    Transmission transmission;
    Weels weels;
    Fuel localFuel;


    public void tankUp(GasStation gasStation, FuelTypes fuelType, int count) {
        localFuel.addFuel(gasStation.takeFuel(count, fuelType));
    }
}
