/*
Лабораторная работа №1
Выполнил студент 321701 группы
Астахов Артём Сергеевич
Вариант 10

Исполняемый файл программы
04.05.2025

Источники:
-Учебно-методическое пособие по ЛОИС
-Шилдт, Герберт Java. Полное руководство. 10-е издание / Герберт Шилдт. – Москва : Компьютерное издательство "Диалектика", 2018. – 1500 с.
*/

package by.astakhau.subformulcounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Условный синтаксис логических выражений:");
            System.out.println("Логическая ложь: 0");
            System.out.println("Логическая истина: 1");
            System.out.println("Логическая конъюнкция: /\\");
            System.out.println("Логическая дизъюнкция: \\/");
            System.out.println("Логическая импликация: ->");
            System.out.println("Логическая эквиваленция: ~");
            System.out.println("Логическое отрицание: !");

            boolean choice = isTestChoose();

            if (choice) {
                var test = new Test();
                test.start();

                System.out.println("Ваша отметка: " + test.getScore());
            } else {

                System.out.println("Введите формулу:");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String formula = reader.readLine();
                boolean protect = Validation.validateInput(new StringBuilder(formula));

                if (protect) {
                    System.out.println(new Counter(formula).getAbsCount());
                } else {
                    System.out.println("формула не корректна");
                }
            }

            System.out.println("Желаете продолжить (Да/нет)?");
            var input = scanner.nextLine().toLowerCase();

            if (input.equals("нет") || input.equals("no") || input.contains("n") || input.contains("н")) {
                System.exit(0);
            }
        }
    }

    private static boolean isTestChoose() {
        Scanner scanner = new Scanner(System.in);
        String input;

        boolean test = false;
        boolean program = false;

        System.out.println("Вы хотите перейти в режим тестирования (да/нет)?");
        input = scanner.nextLine().toLowerCase();

        if (input.equals("да") || input.equals("yes") || input.contains("y") || input.contains("д")) {
            test = true;
            return true;
        }

        System.out.println("Вы хотите перейти в режим нахождения числа подмножеств (да/нет)?");
        input = scanner.nextLine().toLowerCase();

        if (input.equals("да") || input.equals("yes") || input.contains("y") || input.contains("д")) {
            program = true;
            return false;
        }

        if (!program && !test) {
            System.out.println("Программа завершается в связи с отсутствием необходимости в ней");
            System.exit(0);
        }

        return false;
    }
}