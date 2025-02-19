package by.astakhau.carsimulator.cotroller;


import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class CarStateMachine extends AbstractStateMachine<CarStateMachine, CarState, CarEvent, Car> {
    private Driver driver;
    private int actualCarIndex;
    private static final StateMachineBuilder<CarStateMachine, CarState, CarEvent, Car> BUILDER
            = StateMachineBuilderFactory.create(CarStateMachine.class, CarState.class, CarEvent.class, Car.class);

    public CarStateMachine(Driver driver, int actualCarIndex) {
        this.driver = driver;
        this.actualCarIndex = actualCarIndex;
    }

    public void onStartEngine(CarState from, CarState to, CarEvent event, Car context) {
        driver.startVehicle(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    public void onStopEngine(CarState from, CarState to, CarEvent event, Car context) {
        driver.stopVehicle(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    public void onMoveForward(CarState from, CarState to, CarEvent event, Car context) {
        driver.rideForward(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    public void onMoveBackward(CarState from, CarState to, CarEvent event, Car context) {
        driver.rideBackward(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    public void onTurnLeft(CarState from, CarState to, CarEvent event, Car context) {
        driver.turnLeft(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    public void onTurnRight(CarState from, CarState to, CarEvent event, Car context) {
        driver.turnRight(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    public void onStartMaintenance(CarState from, CarState to, CarEvent event, Car context) {
        driver.repair(actualCarIndex);
        SimulatorManager.saveState(driver);
    }

    protected boolean canStartEngine(CarState from, CarState to, CarEvent event, Car context) {
        return context.getLocalFuel().getQuantity() > 0;
    }

    protected boolean isRideForward(CarState from, CarState to, CarEvent event, Car context) {
        return !driver.getCar(actualCarIndex).getMovementState().equals(Car.MovementState.BACK);
    }

    protected boolean isRideBackward(CarState from, CarState to, CarEvent event, Car context) {
        return !driver.getCar(actualCarIndex).getMovementState().equals(Car.MovementState.FORWARD);
    }

    protected boolean isWheelsStraight(CarState from, CarState to, CarEvent event, Car context) {
        return driver.getCar(actualCarIndex).getTurnState().equals(Car.TurnState.STRAIGHT);
    }

    protected boolean isWheelsTurn(CarState from, CarState to, CarEvent event, Car context) {
        return !driver.getCar(actualCarIndex).getTurnState().equals(Car.TurnState.STRAIGHT);
    }

    static {

        BUILDER.externalTransition()
                .from(CarState.STOPPED)
                .to(CarState.IDLE)
                .on(CarEvent.START_ENGINE)
                .whenMvel("canStartEngine")
                .callMethod("onStartEngine");

        BUILDER.externalTransition()
                .from(CarState.IDLE)
                .to(CarState.STOPPED)
                .on(CarEvent.STOP_ENGINE)
                .callMethod("onStopEngine");

        BUILDER.externalTransition()
                .from(CarState.IDLE)
                .to(CarState.MOVING)
                .on(CarEvent.MOVE_FORWARD)
                .whenMvel("isRideForward")
                .callMethod("onMoveForward");

        BUILDER.externalTransition()
                .from(CarState.IDLE)
                .to(CarState.MOVING)
                .on(CarEvent.MOVE_BACKWARD)
                .whenMvel("isRideBackward")
                .callMethod("onMoveBackward");

        BUILDER.externalTransition()
                .from(CarState.MOVING)
                .to(CarState.TURNING)
                .on(CarEvent.TURN_LEFT)
                .whenMvel("isWheelsStraight")
                .callMethod("onTurnLeft");

        BUILDER.externalTransition()
                .from(CarState.MOVING)
                .to(CarState.TURNING)
                .on(CarEvent.TURN_RIGHT)
                .whenMvel("isWheelsStraight")
                .callMethod("onTurnRight");

        BUILDER.externalTransition()
                .from(CarState.TURNING)
                .to(CarState.MOVING)
                .on(CarEvent.SET_WHEELS_STRAIGHT)
                .whenMvel("isWheelsTurn")
                .callMethod("onSetWheelsStraight");

        BUILDER.externalTransition()
    }
}
