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

@StateMachineParameters(stateType = CarState.class, eventType = CarEvent.class, contextType = Car.class)
public class CarStateMachine extends AbstractStateMachine<CarStateMachine, CarState, CarEvent, Car> {
    private Driver driver;
    @Getter private int currentCarIndex;
    private Scanner scanner;
    public static final StateMachineBuilder<CarStateMachine, CarState, CarEvent, Car> BUILDER;

    public CarStateMachine() {
        this.scanner = new Scanner(System.in);
    }

    public void setup(Driver driver, int currentCarIndex) {
        this.driver = driver;
        this.currentCarIndex = currentCarIndex;
        BUILDER.newStateMachine(CarState.WAITING);
    }

    static {
        BUILDER = StateMachineBuilderFactory.create(CarStateMachine.class,
                CarState.class,
                CarEvent.class,
                Car.class);

        // Переходы из WAITING в другие состояния
        BUILDER.externalTransition().from(CarState.WAITING)
                .to(CarState.VEHICLE_CONTROL)
                .on(CarEvent.IN_VEHICLE);

        BUILDER.externalTransition().from(CarState.WAITING)
                .to(CarState.MAINTENANCE)
                .on(CarEvent.IN_MAINTENANCE);

        BUILDER.externalTransition().from(CarState.WAITING)
                .to(CarState.GARAGE_MANAGEMENT)
                .on(CarEvent.ADD_CAR);

        BUILDER.externalTransition().from(CarState.WAITING)
                .to(CarState.CHOOSING_CAR)
                .on(CarEvent.SELECT_CAR);

        BUILDER.externalTransition().from(CarState.WAITING)
                .to(CarState.GARAGE_MANAGEMENT)
                .on(CarEvent.IN_GARAGE);


        // События управления автомобилем (остаются в том же состоянии)
        BUILDER.internalTransition().within(CarState.VEHICLE_CONTROL)
                .on(CarEvent.START_ENGINE)
                .callMethod("onStartEngine");

        BUILDER.internalTransition().within(CarState.VEHICLE_CONTROL)
                .on(CarEvent.MOVE_FORWARD)
                .callMethod("onMoveForward");

        BUILDER.internalTransition().within(CarState.VEHICLE_CONTROL)
                .on(CarEvent.MOVE_BACKWARD)
                .callMethod("onMoveBackward");

        BUILDER.internalTransition().within(CarState.VEHICLE_CONTROL)
                .on(CarEvent.TURN_LEFT)
                .callMethod("onTurnLeft");

        BUILDER.internalTransition().within(CarState.VEHICLE_CONTROL)
                .on(CarEvent.TURN_RIGHT)
                .callMethod("onTurnRight");

        BUILDER.internalTransition().within(CarState.VEHICLE_CONTROL)
                .on(CarEvent.STOP_ENGINE)
                .callMethod("onStopEngine");

        // События обслуживания (остаются в состоянии MAINTENANCE)
        BUILDER.internalTransition().within(CarState.MAINTENANCE)
                .on(CarEvent.CHECK_OIL)
                .callMethod("onCheckOil");

        BUILDER.internalTransition().within(CarState.MAINTENANCE)
                .on(CarEvent.START_REPAIR)
                .callMethod("onStartRepair");

        BUILDER.internalTransition().within(CarState.MAINTENANCE)
                .on(CarEvent.START_REFUEL)
                .callMethod("onStartRefuel");

        // События управления гаражом (остаются в состоянии GARAGE_MANAGEMENT)
        BUILDER.internalTransition().within(CarState.GARAGE_MANAGEMENT)
                .on(CarEvent.ADD_CAR)
                .callMethod("onAddCar");

        BUILDER.internalTransition().within(CarState.GARAGE_MANAGEMENT)
                .on(CarEvent.REMOVE_CAR)
                .callMethod("onRemoveCar");

        BUILDER.internalTransition().within(CarState.GARAGE_MANAGEMENT)
                .on(CarEvent.LIST_CARS)
                .callMethod("onListCars");

        BUILDER.internalTransition().within(CarState.GARAGE_MANAGEMENT)
                .on(CarEvent.SWITCH_CAR)
                .callMethod("onSwitchCar");

        // Возвраты в главное меню
        BUILDER.externalTransition().from(CarState.VEHICLE_CONTROL)
                .to(CarState.WAITING)
                .on(CarEvent.RETURN_TO_MENU);

        BUILDER.externalTransition().from(CarState.MAINTENANCE)
                .to(CarState.WAITING)
                .on(CarEvent.RETURN_TO_MENU);

        BUILDER.externalTransition().from(CarState.GARAGE_MANAGEMENT)
                .to(CarState.WAITING)
                .on(CarEvent.RETURN_TO_MENU);


        // Обработка ошибок
        for (CarState state : CarState.values()) {
            if (state != CarState.ERROR && state != CarState.EXIT) {
                BUILDER.externalTransition().from(state)
                        .to(CarState.ERROR)
                        .on(CarEvent.ERROR_OCCURRED)
                        .callMethod("onError");
            }
        }

        // Выход из программы
        for (CarState state : CarState.values()) {
            if (state != CarState.EXIT) {
                BUILDER.externalTransition().from(state)
                        .to(CarState.EXIT)
                        .on(CarEvent.EXIT_PROGRAM)
                        .callMethod("onExit");
            }
        }
    }


    protected void onStartEngine(CarState from, CarState to, CarEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (currentCar.isRunning()) {
                System.out.println("⚠ Двигатель уже запущен!");
                return;
            }
            driver.startVehicle(currentCarIndex);
            System.out.println("✓ Двигатель успешно запущен");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
        }
    }

    protected void onStopEngine(CarState from, CarState to, CarEvent event, Car context) {
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
            driver.stopVehicle(currentCarIndex);
            System.out.println("✓ Двигатель успешно остановлен");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
        }
    }

    protected void onMoveForward(CarState from, CarState to, CarEvent event, Car context) {
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
            driver.rideForward(currentCarIndex);
            System.out.println("✓ Машина движется вперед");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
        }
    }

    protected void onMoveBackward(CarState from, CarState to, CarEvent event, Car context) {
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
            driver.rideBackward(currentCarIndex);
            System.out.println("✓ Машина движется назад");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
        }
    }

    protected void onTurnLeft(CarState from, CarState to, CarEvent event, Car context) {
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
            driver.turnLeft(currentCarIndex);
            System.out.println("✓ Поворот налево");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
        }
    }

    protected void onTurnRight(CarState from, CarState to, CarEvent event, Car context) {
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
            driver.turnRight(currentCarIndex);
            System.out.println("✓ Поворот направо");
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
        }
    }

    // Методы обслуживания
    protected void onStartRefuel(CarState from, CarState to, CarEvent event, Car context) {
        try {
            System.out.println("Введите количество топлива:");
            int amount = InputValidator.getPositiveIntInput();
            GasStation station = new GasStation();
            driver.tankUp(station, context.getLocalFuel().getFuelType(), amount, currentCarIndex);
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            fire(CarEvent.ERROR_OCCURRED, context);
        }
    }

    protected void onCheckOil(CarState from, CarState to, CarEvent event, Car context) {
        try {
            boolean isOilOk = driver.isOilOK(currentCarIndex);
            System.out.println("Уровень масла " + (isOilOk ? "в норме" : "требует внимания"));
        } catch (Exception e) {
            fire(CarEvent.ERROR_OCCURRED, context);
        }
    }

    protected void onStartRepair(CarState from, CarState to, CarEvent event, Car context) {
        try {
            driver.repair(currentCarIndex);
            SimulatorManager.saveState(driver);
            System.out.println("Ремонт выполнен успешно");
        } catch (Exception e) {
            fire(CarEvent.ERROR_OCCURRED, context);
        }
    }

    // Методы управления гаражом
    protected void onAddCar(CarState from, CarState to, CarEvent event, Car context) {
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

    protected void onRemoveCar(CarState from, CarState to, CarEvent event, Car context) {
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
            driver.removeCar(currentCarIndex);
            SimulatorManager.saveState(driver);

            System.out.println("✓ Автомобиль " + carName + " удален из гаража");
            System.out.println("\nОбновленный список автомобилей:");
            showCarsList();
        } catch (Exception e) {
            System.out.println("⚠ Ошибка при удалении автомобиля: " + e.getMessage());
        }
    }

    protected void onListCars(CarState from, CarState to, CarEvent event, Car context) {
        showCarsList();
    }

    protected void onSwitchCar(CarState from, CarState to, CarEvent event, Car context) {
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
    protected void onError(CarState from, CarState to, CarEvent event, Car context) {
        System.err.println("Произошла ошибка! Возврат в главное меню...");
        fire(CarEvent.RETURN_TO_MENU, context);
    }

    protected void onExit(CarState from, CarState to, CarEvent event, Car context) {
        System.out.println("Выход из программы...");
        SimulatorManager.saveState(driver);
        System.exit(0);
    }

    // Вспомогательные методы
    private void showMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Управление автомобилем");
        System.out.println("2. Обслуживание");
        System.out.println("3. Управление гаражом");
        System.out.println("4. Сменить автомобиль");
        System.out.println("5. Выход");
    }

    private void showVehicleControlMenu() {
        Car currentCar = driver.getCar(currentCarIndex);
        System.out.println("\n=== Статус автомобиля ===");
        System.out.println("Двигатель: " + (currentCar.isRunning() ? "✓ Работает" : "⚫ Остановлен"));
        System.out.println("Движение: " + getMovementStateString(currentCar.getMovementState()));
        System.out.println("Поворот: " + getTurnStateString(currentCar.getTurnState()));

        System.out.println("\n=== Управление автомобилем ===");
        System.out.println("1. Движение вперед");
        System.out.println("2. Движение назад");
        System.out.println("3. Поворот влево");
        System.out.println("4. Поворот вправо");
        System.out.println("5. Заглушить двигатель");
        System.out.println("6. Остановить движение");
        System.out.println("7. Вернуться в главное меню");
        System.out.println("\nВыберите действие:");
    }

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

    private void showMaintenanceMenu() {
        Car currentCar = driver.getCar(currentCarIndex);
        System.out.println("\n=== Состояние автомобиля ===");
        System.out.println("Топливо: " + currentCar.getLocalFuel().getQuantity() + "/" +
                currentCar.getMaxFuel().getQuantity() + " л");

        System.out.println("Масло: " + currentCar.getEngine().getEngineOilQuantity() + "/" +
                currentCar.getEngine().getMaxEngineOilQuantity() + " л");

        System.out.println("Состояние: " + (currentCar.getEngine().isBreading() ? "⚠ Требуется ремонт" : "✓ В порядке"));

        System.out.println("\n=== Обслуживание автомобиля ===");
        System.out.println("1. Заправка");
        System.out.println("2. Проверка масла");
        System.out.println("3. Ремонт");
        System.out.println("4. Вернуться в главное меню");
        System.out.println("\nВыберите действие:");
    }

    // Добавляем метод остановки движения
    protected void onStopMovement(CarState from, CarState to, CarEvent event, Car context) {
        try {
            Car currentCar = driver.getCar(currentCarIndex);
            if (currentCar.getMovementState() == Car.MovementState.STOP) {
                System.out.println("⚠ Автомобиль уже остановлен!");
                showVehicleControlMenu();
                return;
            }
            currentCar.stop();
            System.out.println("✓ Автомобиль остановлен");
            showVehicleControlMenu();
            SimulatorManager.saveState(driver);
        } catch (Exception e) {
            System.out.println("⚠ Ошибка: " + e.getMessage());
            showVehicleControlMenu();
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
            printCarDetails(car);
        } else {
            System.out.println("Всего автомобилей: " + cars.size());
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                System.out.println("\n" + (i + 1) + ". " + car.getName() +
                        ((i + 1) == currentCarIndex ? " (Текущий)" : ""));
                printCarDetails(car);
            }
        }
    }

    private void printCarDetails(Car car) {
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
