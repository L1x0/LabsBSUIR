package org.FirstLabPPOIS;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Dictionary dictionary;

        System.out.println("Введите способ создания словаря (1 - чтение из файла | 2 - чтение первой пары слов из консоли " +
                "| Любая клавиша - создание пустого словаря:");

        switch (sc.next()) {
            case "1" -> dictionary = new Dictionary("input.txt");
            case "2" -> dictionary = new Dictionary(sc.nextLine(), sc.nextLine());
            default -> dictionary = new Dictionary();
        }


        boolean loop = true;

        while (loop) {

            System.out.println("Выбирете операцию над словарём");
            System.out.println("1 - Прочитать файл (input.txt)");
            System.out.println("2 - Добавить из консоли");
            System.out.println("3 - Удалить");
            System.out.println("4 - Поменять перевод английского слова");
            System.out.println("5 - Вывести количество слов");
            System.out.println("6 - Вывести весь словарь");
            System.out.println("7 - Найти слово");
            System.out.println("Любая другая клавиша - Bыход");

            switch (sc.next()) {
                case "1" -> dictionary.addFromFile("input.txt");

                case "2" -> {
 //                   dictionary.addFromConsole();
                }

                case "3" -> {
                    System.out.println("Какое слово удалить (английское)?");
                    dictionary.remove(sc.next());
                }

                case "4" -> {
                    System.out.println("Введите английское слово для замены");
                    String key = sc.next();

                    System.out.println("Введите новый перевод:");
                    dictionary.replace(key, sc.next());
                }

                case "5" -> System.out.println(dictionary.amount());

 //               case "6" -> dictionary.showAll();

                case "7" -> {
                    System.out.println("Введите слово для поиска");
                    sc.reset();
                    dictionary.get(sc.next());
                }

                default -> loop = false;
            }

            System.out.println("############################");

        }
    }
}