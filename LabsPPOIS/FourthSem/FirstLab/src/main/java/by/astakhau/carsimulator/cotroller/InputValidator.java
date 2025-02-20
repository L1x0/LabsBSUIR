package by.astakhau.carsimulator.cotroller;

import lombok.NoArgsConstructor;

import java.util.Scanner;


@NoArgsConstructor
public class InputValidator {

    public static int getIntForMenu() {
        Scanner scanner = new Scanner(System.in);

        try {
            String input = scanner.nextLine().trim();

            // Проверка на пустой ввод
            if (input.isEmpty()) {
                System.out.println("⚠ Ввод не может быть пустым!");
                return -1;
            }

            // Проверка на числовой формат
            if (!input.matches("\\d+")) {
                System.out.println("⚠ Тут может быть только цифра");
                return -1;
            }

            int number = Integer.parseInt(input);

            return number;

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Неожиданная ошибка: " + e.getMessage());
            return -1;
        }

        return -1;
    }

    public static int getPositiveIntInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String input = scanner.nextLine().trim();

                // Проверка на пустой ввод
                if (input.isEmpty()) {
                    System.out.println("⚠ Ввод не может быть пустым!");
                    continue;
                }

                // Проверка на числовой формат
                if (!input.matches("\\d+")) {
                    System.out.println("⚠ Введите только цифры!");
                    continue;
                }

                int number = Integer.parseInt(input);

                // Проверка диапазона
                if (number <= 1) {
                    System.out.printf("⚠ Число должно быть от %d\n", 1);
                    continue;
                }

                return number;

            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠ Неожиданная ошибка: " + e.getMessage());
                return -1;
            }
        }
    }

    public static String getValidStringInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                String input = scanner.nextLine().trim();

                // Проверка на пустой ввод
                if (input.isEmpty()) {
                    System.out.println("⚠ Ввод не может быть пустым!");
                    continue;
                }

                // Проверка максимальной длины
                if (input.length() > 40) {
                    System.out.printf("⚠ Максимальная длина: %d символов!\n", 40);
                    continue;
                }

                // Проверка на специальные символы и цифры в начале строки
                if (!input.matches("^[a-zA-Zа-яА-Я][a-zA-Zа-яА-Я0-9\\s-]*$")) {
                    System.out.println("⚠ Строка должна начинаться с буквы и может содержать только буквы, цифры, пробелы и дефисы!");
                    continue;
                }

                return input;

            } catch (Exception e) {
                System.out.println("⚠ Неожиданная ошибка: " + e.getMessage());
                return null;
            }
        }
    }
}
