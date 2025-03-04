package by.astakhau.carsimulator.cotroller;

import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;

import java.util.Scanner;

public class UI {
    private Driver driver;
    private SimulatorStateMachine stateMachine;
    private Scanner scanner;

    public UI() {
        initializeDriver();
        scanner = new Scanner(System.in);
        initStateMachine();
    }

    private void initializeDriver() {
        driver = SimulatorManager.loadState();
        if (driver == null) {
            driver = new Driver();
            SimulatorManager.saveState(driver);
            driver.setCurrentCarIndex(0);
        }
    }

    private void initStateMachine() {
        stateMachine = SimulatorStateMachine.BUILDER.newStateMachine(SimulatorState.WAITING,
                driver.getCar(driver.getCurrentCarIndex()));

        stateMachine.setup(driver, driver.getCurrentCarIndex());
        Car currentCar = driver.getCar(driver.getCurrentCarIndex());
        stateMachine.start(currentCar);
    }

    public void doProgram() {
        mainLoop();
    }

    private void showWelcomeMessage() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║            CAR SIMULATOR           ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\nТекущий водитель: " + driver.getName() + " " + driver.getLastName());
        System.out.println("Текущий автомобиль: " + driver.getCar(driver.getCurrentCarIndex()).getName());
        stateMachine.fire(SimulatorEvent.SHOW_MENU, driver.getCar(driver.getCurrentCarIndex()));
    }

    private void mainLoop() {
        while (true) {
            try {
                showWelcomeMessage();
                System.out.print("\nВыберите действие: ");
                processUserInput();
            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    private void processUserInput() {
        Car currentCar = driver.getCar(driver.getCurrentCarIndex());

        switch (stateMachine.getCurrentState()) {
            case WAITING:
                processMainMenuChoice(currentCar);
                break;
            case VEHICLE_CONTROL:
                processVehicleControlChoice(currentCar);
                break;
            case MAINTENANCE:
                processMaintenanceChoice(currentCar);
                break;
            case GARAGE_MANAGEMENT:
                processGarageChoice(currentCar);
                break;
            default:
                stateMachine.fire(SimulatorEvent.ERROR_OCCURRED, currentCar);
        }
    }

    private void processMainMenuChoice(Car currentCar) {
        showMainMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // Управление автомобилем
                clearConsole();

                stateMachine.fire(SimulatorEvent.IN_VEHICLE, currentCar);
                break;
            case 2: // Обслуживание
                clearConsole();

                stateMachine.fire(SimulatorEvent.IN_MAINTENANCE, currentCar);
                processMaintenanceChoice(currentCar);
                break;
            case 3: // Управление гаражом
                clearConsole();

                stateMachine.fire(SimulatorEvent.IN_GARAGE, currentCar);
                processGarageChoice(currentCar);
                break;
            case 4: // Выход
                clearConsole();

                stateMachine.fire(SimulatorEvent.EXIT_PROGRAM, currentCar);
                break;
            default:
                clearConsole();

                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void processVehicleControlChoice(Car currentCar) {
        showVehicleControlMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                clearConsole();

                stateMachine.fire(SimulatorEvent.MOVE_FORWARD, currentCar);
                break;
            case 2:
                clearConsole();

                stateMachine.fire(SimulatorEvent.MOVE_BACKWARD, currentCar);
                break;
            case 3:
                clearConsole();

                stateMachine.fire(SimulatorEvent.TURN_LEFT, currentCar);
                break;
            case 4:
                clearConsole();

                stateMachine.fire(SimulatorEvent.TURN_RIGHT, currentCar);
                break;
            case 5:
                clearConsole();

                stateMachine.fire(SimulatorEvent.SET_WHEELS_STRAIGHT, currentCar);
                break;
            case 6:
                clearConsole();

                stateMachine.fire(SimulatorEvent.START_ENGINE, currentCar);
                break;
            case 7:
                clearConsole();

                stateMachine.fire(SimulatorEvent.STOP_ENGINE, currentCar);
                break;
            case 8:
                clearConsole();

                stateMachine.fire(SimulatorEvent.STOP_MOVEMENT, currentCar);
                break;
            case 9:
                clearConsole();

                stateMachine.fire(SimulatorEvent.RETURN_TO_MENU, currentCar);
                break;
            default:
                clearConsole();

                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void processMaintenanceChoice(Car currentCar) {
        showMaintenanceMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // Заправка
                clearConsole();

                stateMachine.fire(SimulatorEvent.START_REFUEL, currentCar);
                break;
            case 2: // Проверка масла
                clearConsole();

                stateMachine.fire(SimulatorEvent.CHECK_OIL, currentCar);
                break;
            case 3: // Ремонт
                clearConsole();

                stateMachine.fire(SimulatorEvent.START_REPAIR, currentCar);
                break;
            case 4: // Вернуться в главное меню
                clearConsole();

                stateMachine.fire(SimulatorEvent.RETURN_TO_MENU, currentCar);
                break;
            default:
                clearConsole();

                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void processGarageChoice(Car currentCar) {
        showGarageMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // Добавить автомобиль
                clearConsole();

                stateMachine.fire(SimulatorEvent.ADD_CAR, currentCar);
                break;
            case 2: // Удалить автомобиль
                clearConsole();

                stateMachine.fire(SimulatorEvent.REMOVE_CAR, currentCar);
                break;
            case 3: // Список автомобилей
                clearConsole();

                stateMachine.fire(SimulatorEvent.LIST_CARS, currentCar);
                break;
            case 4: // Сменить текущий автомобиль
                clearConsole();

                stateMachine.fire(SimulatorEvent.SWITCH_CAR, currentCar);
                driver.setCurrentCarIndex(stateMachine.getCurrentCarIndex());
                break;
            case 5: // Вернуться в главное меню
                clearConsole();

                stateMachine.fire(SimulatorEvent.RETURN_TO_MENU, currentCar);
                break;
            default:
                clearConsole();

                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void showVehicleControlMenu() {
        System.out.println("\n=== Статус автомобиля ===");
        System.out.println("Двигатель: " + (driver.getCar(driver.getCurrentCarIndex())
                .isRunning() ? "✓ Работает" : "⚫ Остановлен"));

        System.out.println("Движение: " + getMovementStateString(driver.getCar(driver.getCurrentCarIndex())
                .getMovementState()));
        System.out.println("Поворот: " + getTurnStateString(driver.getCar(driver.getCurrentCarIndex())
                .getTurnState()));

        System.out.println("\n=== Управление автомобилем ===");
        System.out.println("1. Движение вперед");
        System.out.println("2. Движение назад");
        System.out.println("3. Поворот влево");
        System.out.println("4. Поворот вправо");
        System.out.println("5. Выставить колёса прямо");
        System.out.println("6. Завести двигатель");
        System.out.println("7. Заглушить двигатель");
        System.out.println("8. Остановить движение");
        System.out.println("9. Вернуться в главное меню");
        System.out.println("\nВыберите действие:");
    }

    private void showMaintenanceMenu() {
        Car currentCar = driver.getCar(driver.getCurrentCarIndex());
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

    private void showGarageMenu() {
        System.out.println("\n=== Информация о гараже ===");
        System.out.println("Текущий автомобиль: " + driver.getCar(driver.getCurrentCarIndex()).getName());
        System.out.println("Всего автомобилей: " + driver.getCars().size());

        System.out.println("\nСписок автомобилей:");
        System.out.println(driver.CarsListToString());

        System.out.println("\n=== Управление гаражом ===");
        System.out.println("1. Добавить автомобиль");
        System.out.println("2. Удалить автомобиль");
        System.out.println("3. Список автомобилей");
        System.out.println("4. Сменить текущий автомобиль");
        System.out.println("5. Вернуться в главное меню");
        System.out.println("\nВыберите действие:");
    }

    private void showMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Управление автомобилем");
        System.out.println("2. Обслуживание");
        System.out.println("3. Управление гаражом");
        System.out.println("4. Выход");
    }

    private void handleError(Exception e) {
        System.err.println("\n⚠ Ошибка: " + e.getMessage());
        System.err.println("Пожалуйста, попробуйте снова.");
        scanner.nextLine(); // Очистка буфера
        stateMachine.fire(SimulatorEvent.ERROR_OCCURRED, driver.getCar(driver.getCurrentCarIndex()));
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

    private static void clearConsole() {
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.flush();
    }
}
