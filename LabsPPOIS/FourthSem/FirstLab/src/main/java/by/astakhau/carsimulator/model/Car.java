package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.fuel.Fuel;
import by.astakhau.carsimulator.model.fuel.FuelTypes;
import by.astakhau.carsimulator.model.fuel.GasStation;
import by.astakhau.carsimulator.model.wheels.Wheel;
import by.astakhau.carsimulator.model.wheels.WheelTypes;

public class Car {
    public enum MovementState {
        stop,
        forward,
        back
    }

    public enum TurnState {
        right,
        left,
        straight
    }

    Brakes brakes;
    Engine engine;
    LightIndicators lightIndicator;
    Transmission transmission;
    Fuel localFuel;

    MovementState movementState = MovementState.stop;
    TurnState turnState = TurnState.straight;

    public Car(Brakes brakes, Engine engine, LightIndicators lightIndicator,
               Transmission transmission, Fuel localFuel) {
        this.brakes = brakes;
        this.engine = engine;
        this.lightIndicator = lightIndicator;
        this.transmission = transmission;
        this.localFuel = localFuel;
    }

    public Car() {
        brakes = new Brakes();
        engine = new Engine();
        lightIndicator = new LightIndicators();
        transmission = new Transmission(0, 6);
        localFuel = new Fuel();
    }

    public void tankUp(GasStation gasStation, FuelTypes fuelType, int count) {
        localFuel.addFuel(gasStation.takeFuel(count, fuelType));
    }

    public void startCar() {
        engine.start();
        brakes.setHandbrakeOn(false);
    }

    public void stop() {
        engine.stop();
        movementState = MovementState.stop;
        brakes.setHandbrakeOn(true);
    }

    public void turnRight() {
        turnState = TurnState.right;
        transmission.frontWheels.get(0).setAngle(45);
        transmission.frontWheels.get(0).setAngle(30);
    }

    public void turnLeft() {
        turnState = TurnState.left;
        transmission.frontWheels.get(0).setAngle(30);
        transmission.frontWheels.get(0).setAngle(45);
    }

    public void turnRudderStraight() {
        turnState = TurnState.straight;
        transmission.frontWheels.get(0).setAngle(90);
        transmission.frontWheels.get(0).setAngle(90);
    }

    public void rideForward() {
        movementState = MovementState.forward;
    }

    public void rideBackward() {
        movementState = MovementState.back;
    }

    public void repair() {
        brakes.setActualBrakesDiskTemperature((short) 18);
        brakes.setBreading(false);

        engine.updateOil();
        engine.setBreading(false);

        transmission.frontWheels.removeIf(wheel -> wheel.getOrientation().equals(WheelTypes.back));

        while (transmission.frontWheels.size() < 2) {
            transmission.frontWheels.add(0, new Wheel(WheelTypes.front));
        }

        transmission.setActualGear(0);
        transmission.setMaxGear(6);
        transmission.setBreading(false);
    }

    public double checkOilLevel() {
        return engine.getEngineOilQuantity();
    }

    public void onHeadlights() {
        lightIndicator.setHeadlightsState(true);
    }

    public void offHeadlights() {
        lightIndicator.setHeadlightsState(false);
    }

    public Brakes getBrakes() {
        return brakes;
    }

    public void setBrakes(Brakes brakes) {
        this.brakes = brakes;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public LightIndicators getLightIndicator() {
        return lightIndicator;
    }

    public void setLightIndicator(LightIndicators lightIndicator) {
        this.lightIndicator = lightIndicator;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public Fuel getLocalFuel() {
        return localFuel;
    }

    public void setLocalFuel(Fuel localFuel) {
        this.localFuel = localFuel;
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public void setMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public TurnState getTurnState() {
        return turnState;
    }

    public void setTurnState(TurnState turnState) {
        this.turnState = turnState;
    }
}
