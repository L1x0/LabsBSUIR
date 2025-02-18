package by.astakhau.carsimulator.cotroller;

import by.astakhau.carsimulator.model.Car;
import by.astakhau.carsimulator.model.Driver;
import by.astakhau.carsimulator.model.fuel.FuelTypes;
import by.astakhau.carsimulator.model.fuel.GasStation;

import java.util.Scanner;

public class UI {
    Driver driver;

    public UI(Driver driver) {
        StateManager.clearState();

        this.driver = driver;
        StateManager.saveState(driver);
    }

    public UI() {
        driver = StateManager.loadState();

        if (driver == null) {
            driver = new Driver();

            StateManager.saveState(driver);
        }
    }

    public void doProgram() {
        Scanner scanner = new Scanner(System.in);

        int chosenCar;
        chosenCar = carChoice();

        while (true) {
            int otherCar = 0;
            int choice;

            System.out.println("Выберете операцию из списка:");
            System.out.println("1) Завести автомобиль");
            System.out.println("2) Заглушить автомобиль");
            System.out.println("3) Поехать вперёд");
            System.out.println("4) Поехать назад");
            System.out.println("5) Повернуть вправо");
            System.out.println("6) Повернуть влево");
            System.out.println("7) Заправка");
            System.out.println("8) Проверка уровня масла");
            System.out.println("9) Техническое обслуживание");
            System.out.println("10) Удаление сохранённого состояния");
            System.out.println("11) Добавить автомобиль");
            System.out.println("12) Вывести все машины");
            System.out.println("13) Пересесть на другую машину");
            System.out.println("14) Удалить автомобиль из гаража");
            System.out.println("15) Выход из программы\n\n\n\n");

            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    driver.startVehicle(chosenCar);

                    StateManager.saveState(driver);
                    break;

                case 2:
                    driver.stopVehicle(chosenCar);

                    StateManager.saveState(driver);
                    break;

                case 3:
                    driver.rideForward(chosenCar);

                    StateManager.saveState(driver);
                    break;

                case 4:
                    driver.rideBackward(chosenCar);

                    StateManager.saveState(driver);
                    break;

                case 5:
                    driver.turnRight(chosenCar);

                    StateManager.saveState(driver);
                    break;

                case  6:
                    driver.turnLeft(chosenCar);

                    StateManager.saveState(driver);
                    break;

                case 7:
                    otherCar = carChoice();

                    GasStation station = null;
                    FuelTypes type = null;
                    int count = 0;

                    driver.tankUp(station, type, count, otherCar);

                    StateManager.saveState(driver);
                    break;

                case 8:
                    otherCar = carChoice();

                    driver.isOilOK(otherCar);
                    break;

                case 9:
                    otherCar = carChoice();

                    driver.repair(otherCar);

                    StateManager.saveState(driver);
                    break;

                case 10:
                    StateManager.clearState();
                    break;

                case 11:
                    Car temp = new Car();
                    temp.setName("BIBA");
                    driver.addCar(temp);

                    StateManager.saveState(driver);
                    break;

                case 12:
                    System.out.println(driver.CarsListToString());

                    break;

                case 13:
                    chosenCar = carChoice();

                    break;

                case 14:
                    System.out.println("Выберете машину для удаления, введите её номер");
                    System.out.println(driver.CarsListToString());
                    otherCar = scanner.nextInt();

                    driver.removeCar(otherCar);

                    chosenCar = carChoice();
                    break;

                case 15:
                    System.exit(0);

                default:
                    System.out.println("Введена неверная команда");
                    break;
            }
        }
    }

    public int carChoice() {
        System.out.println("Выберете машину, введите её номер");
        System.out.println(driver.CarsListToString());

        Scanner scanner = new Scanner(System.in);

        return scanner.nextInt();
    }
}
