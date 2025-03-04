package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.fuel.Fuel;
import by.astakhau.carsimulator.model.fuel.FuelTypes;
import by.astakhau.carsimulator.model.fuel.GasStation;
import by.astakhau.carsimulator.model.wheels.Wheel;
import by.astakhau.carsimulator.model.wheels.WheelTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Car {
    public enum MovementState {
        STOP,
        FORWARD,
        BACK
    }

    public enum TurnState {
        RIGHT,
        LEFT,
        STRAIGHT
    }

    private static final int STRAIGHT_WHEEL_ANGLE = 90;

    private Brakes brakes;
    private Engine engine;
    private LightIndicators lightIndicator;
    private Transmission transmission;
    private Fuel localFuel;
    private Fuel maxFuel;
    private String name;

    private MovementState movementState;
    private TurnState turnState;
    private boolean isRunning;

    public Car(String name) {
        brakes = new Brakes();
        engine = new Engine();
        lightIndicator = new LightIndicators();
        transmission = new Transmission(0, 6);
        localFuel = new Fuel();
        maxFuel = new Fuel();

        maxFuel.setQuantity((short) 90);

        this.name = name;
        movementState = MovementState.STOP;
        turnState = TurnState.STRAIGHT;
    }

    public Car() {
        brakes = new Brakes();
        engine = new Engine();
        lightIndicator = new LightIndicators();
        transmission = new Transmission(0, 6);
        localFuel = new Fuel();
        maxFuel = new Fuel();

        maxFuel.setQuantity((short) 90);

        name = "volkswagen passat B6 1.9 TDI";
        movementState = MovementState.STOP;
        turnState = TurnState.STRAIGHT;
    }

    public void tankUp(GasStation gasStation, FuelTypes fuelType, int count) {
        localFuel.addFuel(gasStation.takeFuel(count, fuelType));
    }

    public void startCar() {
        engine.start();
        brakes.setHandbrakeOn(false);
        isRunning = true;
    }

    public void stop() {
        movementState = MovementState.STOP;
        brakes.setHandbrakeOn(true);
    }

    public void stopEngine() {
        engine.stop();
        isRunning = false;
    }

    public void turnRight() {
        turnState = TurnState.RIGHT;
        transmission.frontWheels.get(0).setAngle(45);
        transmission.frontWheels.get(0).setAngle(30);
    }

    public void turnLeft() {
        turnState = TurnState.LEFT;
        transmission.frontWheels.get(0).setAngle(30);
        transmission.frontWheels.get(0).setAngle(45);

        this.lightIndicator.setRightTurnSignal(false);
        this.lightIndicator.setLeftTurnSignal(true);
    }

    public void turnRudderStraight() {
        turnState = TurnState.STRAIGHT;
        transmission.frontWheels.get(0).setAngle(90);
        transmission.frontWheels.get(0).setAngle(90);

        this.lightIndicator.setRightTurnSignal(false);
        this.lightIndicator.setLeftTurnSignal(false);
    }

    public void rideForward() {
        movementState = MovementState.FORWARD;
    }

    public void rideBackward() {
        movementState = MovementState.BACK;
    }

    public void repair() {
        brakes.setActualBrakesDiskTemperature((short) 18);
        brakes.setBreading(false);

        engine.updateOil();
        engine.setBreading(false);

        transmission.frontWheels.removeIf(wheel -> wheel.getOrientation().equals(WheelTypes.BACK));

        while (transmission.frontWheels.size() < 2) {
            transmission.frontWheels.add(0, new Wheel(WheelTypes.FRONT));
        }

        transmission.setActualGear(0);
        transmission.setMaxGear(6);
        transmission.setBreading(false);
    }

    @JsonIgnore
    public boolean isOilLevelOK() {
        return engine.getEngineOilQuantity() < engine.getMaxEngineOilQuantity()
                && engine.getEngineOilQuantity() > engine.getMaxEngineOilQuantity() - 1.5;
    }


    @Override
    public String toString() {
        return "Car{" +
                "brakes=" + brakes +
                ", engine=" + engine +
                ", lightIndicator=" + lightIndicator +
                ", transmission=" + transmission +
                ", localFuel=" + localFuel +
                ", movementState=" + movementState +
                ", turnState=" + turnState +
                '}';
    }
}
