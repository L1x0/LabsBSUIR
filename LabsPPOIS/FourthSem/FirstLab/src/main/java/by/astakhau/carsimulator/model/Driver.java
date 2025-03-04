package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.fuel.FuelTypes;
import by.astakhau.carsimulator.model.fuel.GasStation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Driver {
    private String name;
    private String lastName;
    private int age;
    private boolean driverLicencePresence;
    private List<Car> cars;
    private int currentCarIndex;

    public Driver(String name, String lastName, int age, boolean driverLicencePresence, Car car) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.driverLicencePresence = driverLicencePresence;

        cars = new ArrayList<>();
        cars.add(car);
    }

    public Driver() {
        this.name = "Charles";
        this.lastName = "Leclerc";
        this.age = 27;

        this.driverLicencePresence = true;

        cars = new ArrayList<>();
        cars.add(new Car());
    }

    public String CarsListToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n______________________\n");
        for (int i = 0; i < cars.size(); i++) {
            sb.append(i + 1).append("  ").append(cars.get(i).getName()).append("\n");
        }
        sb.append("______________________\n");

        return sb.toString();
    }

    @JsonIgnore
    public void addCar(Car car) {
        cars.add(car);
    }

    public void removeCar() {
        cars.remove(currentCarIndex);
    }

    public void startVehicle() {
        cars.get(currentCarIndex).startCar();
    }

    public void rideForward() {
        cars.get(currentCarIndex).rideForward();
    }

    public void rideBackward() {
        cars.get(currentCarIndex).rideBackward();
    }

    public void turnRight() {
        cars.get(currentCarIndex).turnRight();
    }

    public void turnLeft() {
        cars.get(currentCarIndex).turnLeft();
    }

    public void setWheelsStraight() {
        cars.get(currentCarIndex).setTurnState(Car.TurnState.STRAIGHT);
    }

    public void stopVehicle() {
        cars.get(currentCarIndex).stop();
    }

    public void stopEngine() {
        cars.get(currentCarIndex).stopEngine();
    }

    @JsonIgnore
    public boolean isOilOK() {
        return cars.get(currentCarIndex).isOilLevelOK();
    }

    public void repair() {
        cars.get(currentCarIndex).repair();
    }

    public void tankUp(GasStation gasStation, FuelTypes type, int count) {
        cars.get(currentCarIndex).tankUp(gasStation, type, count);
    }

    public void setCar(Car car) {
        cars.set(currentCarIndex, car);
    }

    public Car getCar(int index) {
        return cars.get(index);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", driverLicencePresence=" + driverLicencePresence +
                ", cars=" + cars.toString() +
                '}';
    }
}
