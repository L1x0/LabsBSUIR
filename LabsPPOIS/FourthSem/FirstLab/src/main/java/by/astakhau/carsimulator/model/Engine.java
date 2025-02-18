package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.fuel.FuelTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Engine {
    enum State {
        stopped,
        launched
    }

    private String engineName;
    private double volume;
    private double consumption;
    private double engineOilQuantity;
    private double maxEngineOilQuantity;
    private boolean breading;
    private FuelTypes fuelType;
    private State state;

    public Engine() {
        volume = 1.9;
        engineName = "1.9 TDI";
        consumption = 7.3;
        fuelType = FuelTypes.diesel;
        engineOilQuantity = 3.6;
        breading = false;
        maxEngineOilQuantity = 4.5;

        state = State.stopped;
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

    public void updateOil() {
        engineOilQuantity = maxEngineOilQuantity - 1;
    }

    public void addOil(double quantity) {
        engineOilQuantity += quantity;
        breading = engineOilQuantity > maxEngineOilQuantity;
    }
}
