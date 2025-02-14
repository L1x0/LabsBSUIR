package by.astakhau.carsimulator.model;

public class Driver {
    String name;
    String lastName;

    int age;

    boolean driverLicencePresence;

    Car car;

    public Driver(String name, String lastName, int age, boolean driverLicencePresence, Car car) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.driverLicencePresence = driverLicencePresence;
        this.car = car;
    }

    public Driver() {
        this.name = "Charles";
        this.lastName = "Leclerc";
        this.age = 27;

        this.driverLicencePresence = true;

        car = new Car();
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setDriverLicencePresence(boolean driverLicencePresence) {
        this.driverLicencePresence = driverLicencePresence;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public boolean isDriverLicencePresence() {
        return driverLicencePresence;
    }

    public Car getCar() {
        return car;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", driverLicencePresence=" + driverLicencePresence +
                ", car=" + car.toString() +
                '}';
    }
}
