package by.astakhau.carsimulator.model.fuel;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Fuel {
    private FuelTypes fuelType;
    private short quantity;
    private int octaneNumber;

    public Fuel(FuelTypes fuelType, short quantity, int octaneNumber) {
        this.fuelType = fuelType;
        this.quantity = quantity;
        this.octaneNumber = octaneNumber;
    }

    public Fuel() {
        this.fuelType = FuelTypes.petrol;
        this.quantity = 50;
        this.octaneNumber = 95;
    }

    public void addFuel(int count) {
        quantity += count;
    }
}
