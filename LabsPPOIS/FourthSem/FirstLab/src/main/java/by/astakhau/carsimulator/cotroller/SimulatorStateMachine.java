package by.astakhau.carsimulator.cotroller;

import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;
import by.astakhau.carsimulator.model.fuel.GasStation;
import lombok.Getter;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;


import java.util.Scanner;
import java.util.List;

@StateMachineParameters(stateType = SimulatorState.class, eventType = SimulatorEvent.class, contextType = Car.class)
public class SimulatorStateMachine extends AbstractStateMachine<SimulatorStateMachine, SimulatorState, SimulatorEvent, Car> {
    private Driver driver;
    @Getter
    private int currentCarIndex;
    private Scanner scanner;
    public static final StateMachineBuilder<SimulatorStateMachine, SimulatorState, SimulatorEvent, Car> BUILDER;

    public SimulatorStateMachine() {
        this.scanner = new Scanner(System.in);
    }

    public void setup(Driver driver, int currentCarIndex) {
        this.driver = driver;
        this.currentCarIndex = currentCarIndex;
        BUILDER.newStateMachine(SimulatorState.WAITING);
    }

    static {
        BUILDER = StateMachineBuilderFactory.create(SimulatorStateMachine.class,
                SimulatorState.class,
                SimulatorEvent.class,
                Car.class);

        // Переходы из WAITING в другие состояния
        BUILDER.externalTransition().from(SimulatorState.WAITING)
                .to(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.IN_VEHICLE);

        BUILDER.externalTransition().from(SimulatorState.WAITING)
                .to(SimulatorState.MAINTENANCE)
                .on(SimulatorEvent.IN_MAINTENANCE);

        BUILDER.externalTransition().from(SimulatorState.WAITING)
                .to(SimulatorState.GARAGE_MANAGEMENT)
                .on(SimulatorEvent.ADD_CAR);

        BUILDER.externalTransition().from(SimulatorState.WAITING)
                .to(SimulatorState.GARAGE_MANAGEMENT)
                .on(SimulatorEvent.IN_GARAGE);

        // События управления автомобилем (остаются в том же состоянии)
        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.START_ENGINE)
                .callMethod("onStartEngine");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.MOVE_FORWARD)
                .callMethod("onMoveForward");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.MOVE_BACKWARD)
                .callMethod("onMoveBackward");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.STOP_MOVEMENT)
                .callMethod("onStopMovement");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.TURN_LEFT)
                .callMethod("onTurnLeft");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.TURN_RIGHT)
                .callMethod("onTurnRight");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.SET_WHEELS_STRAIGHT)
                .callMethod("onSetWheelsStraight");

        BUILDER.internalTransition().within(SimulatorState.VEHICLE_CONTROL)
                .on(SimulatorEvent.STOP_ENGINE)
                .callMethod("onStopEngine");

        // События обслуживания (остаются в состоянии MAINTENANCE)
        BUILDER.internalTransition().within(SimulatorState.MAINTENANCE)
                .on(SimulatorEvent.CHECK_OIL)
                .callMethod("onCheckOil");

        BUILDER.internalTransition().within(SimulatorState.MAINTENANCE)
                .on(SimulatorEvent.START_REPAIR)
                .callMethod("onStartRepair");

        BUILDER.internalTransition().within(SimulatorState.MAINTENANCE)
                .on(SimulatorEvent.START_REFUEL)
                .callMethod("onStartRefuel");

        // События управления гаражом (остаются в состоянии GARAGE_MANAGEMENT)
        BUILDER.internalTransition().within(SimulatorState.GARAGE_MANAGEMENT)
                .on(SimulatorEvent.ADD_CAR)
                .callMethod("onAddCar");

        BUILDER.internalTransition().within(SimulatorState.GARAGE_MANAGEMENT)
                .on(SimulatorEvent.REMOVE_CAR)
                .callMethod("onRemoveCar");

        BUILDER.internalTransition().within(SimulatorState.GARAGE_MANAGEMENT)
                .on(SimulatorEvent.LIST_CARS)
                .callMethod("onListCars");

        BUILDER.internalTransition().within(SimulatorState.GARAGE_MANAGEMENT)
                .on(SimulatorEvent.SWITCH_CAR)
                .callMethod("onSwitchCar");

        // Возвраты в главное меню
        BUILDER.externalTransition().from(SimulatorState.VEHICLE_CONTROL)
                .to(SimulatorState.WAITING)
                .on(SimulatorEvent.RETURN_TO_MENU);

        BUILDER.externalTransition().from(SimulatorState.MAINTENANCE)
                .to(SimulatorState.WAITING)
                .on(SimulatorEvent.RETURN_TO_MENU);

        BUILDER.externalTransition().from(SimulatorState.GARAGE_MANAGEMENT)
                .to(SimulatorState.WAITING)
                .on(SimulatorEvent.RETURN_TO_MENU);


        // Обработка ошибок
        for (SimulatorState state : SimulatorState.values()) {
            if (state != SimulatorState.ERROR && state != SimulatorState.EXIT) {
                BUILDER.externalTransition().from(state)
                        .to(SimulatorState.ERROR)
                        .on(SimulatorEvent.ERROR_OCCURRED)
                        .callMethod("onError");
            }
        }

        // Выход из программы
        for (SimulatorState state : SimulatorState.values()) {
            if (state != SimulatorState.EXIT) {
                BUILDER.externalTransition().from(state)
                        .to(SimulatorState.EXIT)
                        .on(SimulatorEvent.EXIT_PROGRAM)
                        .callMethod("onExit");
            }
        }
    }


    protected void onStartEngine(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (currentCar.isRunning()) {
                System.out.println("⚠ Двигатель уже запущен!");
                return;
            }
            driver.startVehicle();
            System.out.println("✓ Двигатель успешно запущен");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
        }
    }

    protected void onStopEngine(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (!currentCar.isRunning()) {
                System.out.println("⚠ Двигатель уже остановлен!");
                return;
            }
            if (currentCar.getMovementState() != Car.MovementState.STOP) {
                System.out.println("⚠ Нельзя заглушить двигатель во время движения!");
                return;
            }
            driver.stopEngine();
            System.out.println("✓ Двигатель успешно остановлен");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    protected void onMoveForward(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (!currentCar.isRunning()) {
                System.out.println("⚠ Сначала нужно запустить двигатель!");
                return;
            }
            if (currentCar.getMovementState() == Car.MovementState.BACK) {
                System.out.println("⚠ Сначала остановите движение назад!");
                return;
            }
            driver.rideForward();
            System.out.println("✓ Машина движется вперед");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    protected void onMoveBackward(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (!currentCar.isRunning()) {
                System.out.println("⚠ Сначала нужно запустить двигатель!");
                return;
            }
            if (currentCar.getMovementState() == Car.MovementState.FORWARD) {
                System.out.println("⚠ Сначала остановите движение вперед!");
                return;
            }
            driver.rideBackward();
            System.out.println("✓ Машина движется назад");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    protected void onTurnLeft(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (!currentCar.isRunning()) {
                System.out.println("⚠ Сначала нужно запустить двигатель!");
                return;
            }
            if (currentCar.getTurnState() == Car.TurnState.RIGHT) {
                System.out.println("⚠ Сначала выровняйте колеса!");
                return;
            }
            driver.turnLeft();
            System.out.println("✓ Поворот налево");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    protected void onTurnRight(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (!currentCar.isRunning()) {
                System.out.println("⚠ Сначала нужно запустить двигатель!");
                return;
            }
            if (currentCar.getTurnState() == Car.TurnState.LEFT) {
                System.out.println("⚠ Сначала выровняйте колеса!");
                return;
            }
            driver.turnRight();
            System.out.println("✓ Поворот направо");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    protected void onSetWheelsStraight(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (!currentCar.isRunning()) {
                System.out.println("⚠ Сначала нужно запустить двигатель!");
                return;
            }
            if (currentCar.getTurnState() == Car.TurnState.STRAIGHT) {
                System.out.println("⚠ Колёса и так стоят прямо!");
                return;
            }
            driver.setWheelsStraight();
            System.out.println("✓ Колёса выставлены прямо");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    // Методы обслуживания
    protected void onStartRefuel(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            System.out.println("Введите количество топлива:");
            int amount = InputValidator.getPositiveIntInput();
            GasStation station = new GasStation();
            driver.tankUp(station, context.getLocalFuel().getFuelType(), amount);
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            fire(SimulatorEvent.ERROR_OCCURRED, context);
        }
    }

    protected void onCheckOil(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            boolean isOilOk = driver.isOilOK();
            System.out.println("Уровень масла " + (isOilOk ? "в норме" : "требует внимания"));
        } catch (Exception e) {
            fire(SimulatorEvent.ERROR_OCCURRED, context);
        }
    }

    protected void onStartRepair(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            driver.repair();
            SimulatorManager.saveState(driver);
            System.out.println("Ремонт выполнен успешно");
        } catch (Exception e) {
            fire(SimulatorEvent.ERROR_OCCURRED, context);
        }
    }

    // Методы управления гаражом
    protected void onAddCar(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            System.out.println("\n=== Добавление нового автомобиля ===");
            System.out.println("Введите название автомобиля:");
            String name = InputValidator.getValidStringInput();

            Car newCar = new Car(name);
            driver.addCar(newCar);
            SimulatorManager.saveState(driver);

            System.out.println("✓ Автомобиль " + name + " успешно добавлен в гараж");
            System.out.println("\nОбновленный список автомобилей:");
            showCarsList();
        } catch (Exception e) {
            System.out.println("⚠ Ошибка при добавлении автомобиля: " + e.getMessage());
        }
    }

    protected void onRemoveCar(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            System.out.println("\n=== Удаление автомобиля ===");
            showCarsList();

            if (driver.getCars().size() <= 1) {
                System.out.println("⚠ Нельзя удалить единственный автомобиль!");
                return;
            }

            System.out.println("\nВведите номер автомобиля для удаления:");
            int carIndex = InputValidator.getPositiveIntInput() - 1;

            if (carIndex < 1 || carIndex > driver.getCars().size()) {
                System.out.println("⚠ Неверный номер автомобиля!");
                return;
            }

            if (carIndex == currentCarIndex) {
                System.out.println("⚠ Нельзя удалить текущий автомобиль! Сначала выберите другой.");
                return;
            }

            currentCarIndex = carIndex;

            String carName = driver.getCar(currentCarIndex).getName();
            driver.removeCar();
            SimulatorManager.saveState(driver);

            System.out.println("✓ Автомобиль " + carName + " удален из гаража");
            System.out.println("\nОбновленный список автомобилей:");
            showCarsList();
        } catch (Exception e) {
            System.out.println("⚠ Ошибка при удалении автомобиля: " + e.getMessage());
        }
    }

    protected void onListCars(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        showCarsList();
    }

    protected void onSwitchCar(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            System.out.println("\n=== Выбор автомобиля ===");
            showCarsList();

            if (driver.getCars().size() <= 1) {
                System.out.println("⚠ В гараже только один автомобиль!");
                return;
            }

            System.out.println("\nВведите номер автомобиля:");
            int newCarIndex = InputValidator.getPositiveIntInput();

            if (newCarIndex < 1 || newCarIndex > driver.getCars().size()) {
                System.out.println("⚠ Неверный номер автомобиля!");
                return;
            }

            if (newCarIndex == currentCarIndex) {
                System.out.println("⚠ Этот автомобиль уже выбран!");
                return;
            }

            currentCarIndex = newCarIndex - 1;
            Car newCar = driver.getCar(currentCarIndex);
            System.out.println("✓ Выбран автомобиль: " + newCar.getName());
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка при смене автомобиля: " + e.getMessage());
        }
    }


    // Системные методы
    protected void onError(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        System.err.println("Произошла ошибка! Возврат в главное меню...");
        fire(SimulatorEvent.RETURN_TO_MENU, context);
    }

    protected void onExit(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        System.out.println("Выход из программы...");
        SimulatorManager.saveState(driver);
        System.exit(0);
    }

    // Вспомогательные методы
    private String getMovementStateString(Car.MovementState state) {
        switch (state) {
            case FORWARD:
                return "➡ Движение вперед";
            case BACK:
                return "⬅ Движение назад";
            default:
                return "⚫ Остановлен";
        }
    }

    private String getTurnStateString(Car.TurnState state) {
        switch (state) {
            case LEFT:
                return "↖ Влево";
            case RIGHT:
                return "↗ Вправо";
            default:
                return "⬆ Прямо";
        }
    }

    // Добавляем метод остановки движения
    protected void onStopMovement(SimulatorState from, SimulatorState to, SimulatorEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (currentCar.getMovementState() == Car.MovementState.STOP) {
                System.out.println("⚠ Автомобиль уже остановлен!");

                return;
            }
            driver.stopVehicle();
            System.out.println("✓ Автомобиль остановлен");

            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());

        }
    }

    private void showCarsList() {
        List<Car> cars = driver.getCars();
        System.out.println("\n=== Автомобили в гараже ===");

        if (cars.isEmpty()) {
            System.out.println("Гараж пуст");
            return;
        }

        if (cars.size() == 1) {
            Car car = cars.get(0);
            System.out.println("В гараже только один автомобиль:");
            System.out.println("1. " + car.getName() + (1 == currentCarIndex ? " (Текущий)" : ""));
            printCarDetails();
        } else {
            System.out.println("Всего автомобилей: " + cars.size());
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                System.out.println("\n" + (i + 1) + ". " + car.getName() +
                        ((i + 1) == currentCarIndex ? " (Текущий)" : ""));
                printCarDetails();
            }
        }
    }

    private void printCarDetails() {
        Car car = driver.getCar(currentCarIndex);
        System.out.println("   Состояние двигателя: " +
                (car.isRunning() ? "✓ Работает" : "⚫ Остановлен"));
        System.out.println("   Топливо: " + car.getLocalFuel().getQuantity() + "/" +
                car.getMaxFuel().getQuantity() + " л");
        System.out.println("   Масло: " + car.getEngine().getEngineOilQuantity() + "/" +
                car.getEngine().getMaxEngineOilQuantity() + " л");
        System.out.println("   Техническое состояние: " +
                (car.getEngine().isBreading() ? "⚠ Требуется ремонт" : "✓ В порядке"));
    }
}
