package by.astakhau.carsimulator;

import by.astakhau.carsimulator.fuel.FuelTypes;

public class Engine {
    double volume;
    String engineName;
    double consumption;

    FuelTypes fuelType;

    public Engine(double volume, String engineName, double consumption, FuelTypes fuelType) {
        this.volume = volume;
        this.engineName = engineName;
        this.consumption = consumption;
        this.fuelType = fuelType;
    }

    public Engine() {
        volume = 1.9;
        engineName = "1.9 TDI";
        
    }
}
