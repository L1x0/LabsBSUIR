package by.astakhau.carsimulator.controller;

import by.astakhau.carsimulator.cotroller.SimulatorEvent;
import by.astakhau.carsimulator.cotroller.SimulatorManager;
import by.astakhau.carsimulator.cotroller.SimulatorState;
import by.astakhau.carsimulator.cotroller.SimulatorStateMachine;
import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SimulatorStateMachineTest {
    Driver driver;
    SimulatorStateMachine stateMachine;

    @BeforeEach
    public void setUp() {
        Driver driver;
        SimulatorStateMachine stateMachine;
        Scanner scanner;

        initializeDriver();
        scanner = new Scanner(System.in);
        initStateMachine();
    }

    private void initializeDriver() {
        driver = new Driver();
        SimulatorManager.saveState(driver);
        driver.setCurrentCarIndex(0);
    }

    private void initStateMachine() {
        stateMachine = SimulatorStateMachine.BUILDER.newStateMachine(SimulatorState.WAITING,
                driver.getCar(driver.getCurrentCarIndex()));

        stateMachine.setup(driver, driver.getCurrentCarIndex());
        Car currentCar = driver.getCar(driver.getCurrentCarIndex());
        stateMachine.start(currentCar);
    }

    @Test
    public void maintenanceEntrance() {
        stateMachine.fire(SimulatorEvent.SHOW_MENU, driver.getCar(driver.getCurrentCarIndex()));
        assertEquals(stateMachine.getCurrentState(), SimulatorState.WAITING);
    }

    @Test
    public void returnToMenuTest() {
        stateMachine.fire(SimulatorEvent.RETURN_TO_MENU, driver.getCar(driver.getCurrentCarIndex()));
        assertEquals(stateMachine.getCurrentState(), SimulatorState.WAITING);
    }

    @Test
    public void garageEntrance() {
        stateMachine.fire(SimulatorEvent.IN_GARAGE, driver.getCar(driver.getCurrentCarIndex()));
        assertEquals(stateMachine.getCurrentState(), SimulatorState.GARAGE_MANAGEMENT);
    }

    @Test
    public void vehicleControlEntrance() {
        stateMachine.fire(SimulatorEvent.IN_VEHICLE, driver.getCar(driver.getCurrentCarIndex()));
        assertEquals(stateMachine.getCurrentState(), SimulatorState.VEHICLE_CONTROL);
    }

    @Test
    public void engineControlTest() {
        stateMachine.fire(SimulatorEvent.IN_VEHICLE, driver.getCar(driver.getCurrentCarIndex()));

        stateMachine.fire(SimulatorEvent.START_ENGINE, driver.getCar(driver.getCurrentCarIndex()));
        assertTrue(driver.getCar(0).isRunning());

        stateMachine.fire(SimulatorEvent.STOP_ENGINE, driver.getCar(driver.getCurrentCarIndex()));
        assertFalse(driver.getCar(0).isRunning());
    }
}
