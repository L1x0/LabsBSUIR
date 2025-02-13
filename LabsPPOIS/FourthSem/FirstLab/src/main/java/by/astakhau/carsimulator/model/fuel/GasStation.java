package by.astakhau.carsimulator.model.fuel;

public class GasStation {
    int quantityOfExistingPetrol;
    int quantityOfExistingDiesel;
    int quantityOfStations;

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

    public int getQuantityOfExistingDiesel() {
        return quantityOfExistingDiesel;
    }

    public void setQuantityOfExistingDiesel(int quantityOfExistingDiesel) {
        this.quantityOfExistingDiesel = quantityOfExistingDiesel;
    }

    public int getQuantityOfExistingPetrol() {
        return quantityOfExistingPetrol;
    }

    public void setQuantityOfExistingPetrol(int quantityOfExistingPetrol) {
        this.quantityOfExistingPetrol = quantityOfExistingPetrol;
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
