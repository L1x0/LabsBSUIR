package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.fuel.FuelTypes;

public class Engine {
    enum State {
        stopped,
        launched
    }

    String engineName;

    double volume;
    double consumption;
    double engineOilQuantity;
    double maxEngineOilQuantity;

    boolean breading;

    FuelTypes fuelType;
    State state;

    public Engine(double volume, String engineName, double consumption,
                  FuelTypes fuelType, double engineOilQuantity, double maxEngineOilQuantity) {
        this.volume = volume;
        this.engineName = engineName;
        this.consumption = consumption;
        this.fuelType = fuelType;
        this.engineOilQuantity = engineOilQuantity;
        this.maxEngineOilQuantity = maxEngineOilQuantity;

        this.breading = engineOilQuantity > maxEngineOilQuantity;
    }

    public Engine() {
        volume = 1.9;
        engineName = "1.9 TDI";
        consumption = 7.3;
        fuelType = FuelTypes.diesel;
        engineOilQuantity = 3.6;
        breading = false;
        maxEngineOilQuantity = 4.5;
    }

    public boolean isBreading() {
        return breading;
    }

    public void setBreading(boolean breading) {
        this.breading = breading;
    }

    public void start() {
        if (state == State.stopped) {
            state = State.launched;
        } else {
            throw new IllegalStateException("Engine already started");
        }
    }

    public void stop() {
        if (state == State.launched) {
            state = State.stopped;
        } else {
            throw new IllegalStateException("Engine already stopped");
        }
    }

    public double getOilLevel() {
        return engineOilQuantity;
    }

    public void updateOil() {
        engineOilQuantity = maxEngineOilQuantity - 1;
    }

    public void addOil(double quantity) {
        engineOilQuantity += quantity;
        breading = engineOilQuantity > maxEngineOilQuantity;
    }
}
