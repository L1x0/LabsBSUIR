package by.astakhau.carsimulator.model.fuel;

public class Fuel {
    FuelTypes fuelType;
    short quantity;
    public int octaneNumber;

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

    public FuelTypes getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelTypes fuelType) {
        this.fuelType = fuelType;
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public int getOctaneNumber() {
        return octaneNumber;
    }

    public void setOctaneNumber(int octaneNumber) {
        this.octaneNumber = octaneNumber;
    }

    public void addFuel(int count) {
        quantity += count;
    }
}
