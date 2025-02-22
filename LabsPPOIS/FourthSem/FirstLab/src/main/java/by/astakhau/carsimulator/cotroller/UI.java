package by.astakhau.carsimulator.cotroller;

import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;
import java.util.Scanner;

public class UI {
    private Driver driver;
    private CarStateMachine stateMachine;
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
            driver.setCurrentCarIndex(1);
        }
    }

    private void initStateMachine() {
        stateMachine = CarStateMachine.BUILDER.newStateMachine(CarState.WAITING,
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
        stateMachine.fire(CarEvent.SHOW_MENU, driver.getCar(driver.getCurrentCarIndex()));
    }

    private void mainLoop() {
        while (true) {
            try {
                clearConsole();
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
                stateMachine.fire(CarEvent.ERROR_OCCURRED, currentCar);
        }
    }

    private void processMainMenuChoice(Car currentCar) {
        showMainMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // Управление автомобилем
                stateMachine.fire(CarEvent.IN_VEHICLE, currentCar);
                break;
            case 2: // Обслуживание
                stateMachine.fire(CarEvent.IN_MAINTENANCE, currentCar);
                processMaintenanceChoice(currentCar);
                break;
            case 3: // Управление гаражом
                stateMachine.fire(CarEvent.IN_GARAGE, currentCar);
                processGarageChoice(currentCar);
                break;
            case 4: // Выход
                stateMachine.fire(CarEvent.EXIT_PROGRAM, currentCar);
                break;
            default:
                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void processVehicleControlChoice(Car currentCar) {
        showVehicleControlMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                stateMachine.fire(CarEvent.START_ENGINE, currentCar);
                break;
            case 2: // Заглушить двигатель
                stateMachine.fire(CarEvent.STOP_ENGINE, currentCar);
                break;
            case 3: // Движение вперед
                stateMachine.fire(CarEvent.MOVE_FORWARD, currentCar);
                break;
            case 4: // Движение назад
                stateMachine.fire(CarEvent.MOVE_BACKWARD, currentCar);
                break;
            case 5: // Поворот влево
                stateMachine.fire(CarEvent.TURN_LEFT, currentCar);
                break;
            case 6: // Поворот вправо
                stateMachine.fire(CarEvent.TURN_RIGHT, currentCar);
                break;
            case 7: // Вернуться в главное меню
                stateMachine.fire(CarEvent.RETURN_TO_MENU, currentCar);
                break;
            default:
                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void processMaintenanceChoice(Car currentCar) {
        showMaintenanceMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // Заправка
                stateMachine.fire(CarEvent.START_REFUEL, currentCar);
                break;
            case 2: // Проверка масла
                stateMachine.fire(CarEvent.CHECK_OIL, currentCar);
                break;
            case 3: // Ремонт
                stateMachine.fire(CarEvent.START_REPAIR, currentCar);
                break;
            case 4: // Вернуться в главное меню
                stateMachine.fire(CarEvent.RETURN_TO_MENU, currentCar);
                break;
            default:
                System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void processGarageChoice(Car currentCar) {
        showGarageMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // Добавить автомобиль
                stateMachine.fire(CarEvent.ADD_CAR, currentCar);
                break;
            case 2: // Удалить автомобиль
                stateMachine.fire(CarEvent.REMOVE_CAR, currentCar);
                break;
            case 3: // Список автомобилей
                stateMachine.fire(CarEvent.LIST_CARS, currentCar);
                break;
            case 4: // Сменить текущий автомобиль
                stateMachine.fire(CarEvent.SWITCH_CAR, currentCar);
                driver.setCurrentCarIndex(stateMachine.getCurrentCarIndex());
                break;
            case 5: // Вернуться в главное меню
                stateMachine.fire(CarEvent.RETURN_TO_MENU, currentCar);
                break;
            default:
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
        System.out.println("5. Заглушить двигатель");
        System.out.println("6. Остановить движение");
        System.out.println("7. Вернуться в главное меню");
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
        stateMachine.fire(CarEvent.ERROR_OCCURRED, driver.getCar(driver.getCurrentCarIndex()));
    }

    private String getMovementStateString(Car.MovementState state) {
        switch (state) {
            case FORWARD: return "➡ Движение вперед";
            case BACK: return "⬅ Движение назад";
            default: return "⚫ Остановлен";
        }
    }

    private String getTurnStateString(Car.TurnState state) {
        switch (state) {
            case LEFT: return "↖ Влево";
            case RIGHT: return "↗ Вправо";
            default: return "⬆ Прямо";
        }
    }

    private static void clearConsole() {
        // ANSI escape код для очистки экрана и перемещения курсора в верхний левый угол
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.flush();
    }
}
