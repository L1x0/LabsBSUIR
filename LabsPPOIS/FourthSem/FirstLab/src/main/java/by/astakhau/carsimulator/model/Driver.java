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

    public void removeCar(int number) {
        cars.remove(number - 1);
    }

    public void startVehicle(int number) {
        cars.get(number - 1).startCar();
    }

    public void rideForward(int number) {
        cars.get(number - 1).rideForward();
    }

    public void rideBackward(int number) {
        cars.get(number - 1).rideBackward();
    }

    public void turnRight(int number) {
        cars.get(number - 1).turnRight();
    }

    public void turnLeft(int number) {
        cars.get(number - 1).turnLeft();
    }

    public void stopVehicle(int number) {
        cars.get(number - 1).stop();
    }

    @JsonIgnore
    public boolean isOilOK(int number) {
        return cars.get(number - 1).isOilLevelOK();
    }

    public void repair(int number) {
        cars.get(number - 1).repair();
    }

    public void tankUp(GasStation gasStation, FuelTypes type, int count, int number) {
        cars.get(number - 1).tankUp(gasStation, type, count);
    }

    public void setCar(Car car, int number) {
        cars.set(number - 1, car);
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
