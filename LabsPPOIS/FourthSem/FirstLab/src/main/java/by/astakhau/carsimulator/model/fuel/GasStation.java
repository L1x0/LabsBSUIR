package by.astakhau.carsimulator.model.fuel;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GasStation {
    private int quantityOfExistingPetrol;
    private int quantityOfExistingDiesel;
    private int quantityOfStations;

    public GasStation(int quantityOfExistingPetrol, int quantityOfExistingDiesel, int quantityOfStations) {
        this.quantityOfExistingPetrol = quantityOfExistingPetrol;
        this.quantityOfExistingDiesel = quantityOfExistingDiesel;
        this.quantityOfStations = quantityOfStations;
    }

    public GasStation() {
        this.quantityOfExistingPetrol = 500;
        this.quantityOfExistingDiesel = 500;
        this.quantityOfStations = 6;
    }

    public int takeFuel(int count, FuelTypes fuelType) {
        if (fuelType.equals(FuelTypes.diesel)) {
            if (quantityOfExistingDiesel < count) {
                quantityOfExistingDiesel = 0;

                return quantityOfExistingDiesel;
            } else {
                quantityOfExistingDiesel -= count;
                return count;
            }
        } else {
            if (quantityOfExistingPetrol < count) {
                quantityOfExistingPetrol = 0;
                return quantityOfExistingPetrol;
            } else {
                quantityOfExistingPetrol -= count;
                return count;
            }
        }
    }
}
