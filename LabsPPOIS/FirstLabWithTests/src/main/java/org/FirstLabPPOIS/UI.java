package org.FirstLabPPOIS;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UI {
    Dictionary dictionary = new Dictionary();
    Scanner sc = new Scanner(System.in);


    public UI () {
        System.out.println("Введите способ создания словаря (1 - чтение из файла | 2 - чтение первой пары слов из консоли " +
                "| Любая клавиша - создание пустого словаря:");

        switch (sc.next()) {
            case "1" -> {
                InputStream ioStream = this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("input.txt");

                assert ioStream != null;

                Scanner scannerForFile = new Scanner(ioStream);

                while (scannerForFile.hasNextLine()) {
                    String[] str = scannerForFile.nextLine().split(" ");

                    try {
                        dictionary.add(str[0], str[1]);
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            case "2" -> dictionary.add(sc.next(), sc.next());

            default -> System.out.println("Словарь пуст");
        }


        boolean loop = true;

        while (loop) {

            System.out.println("Выбирете операцию над словарём");
            System.out.println("1 - Добавить из консоли");
            System.out.println("2 - Удалить");
            System.out.println("3 - Поменять перевод английского слова");
            System.out.println("4 - Вывести количество слов");
            System.out.println("5 - Вывести весь словарь");
            System.out.println("6 - Найти слово");
            System.out.println("Любая другая клавиша - Bыход");

            switch (sc.next()) {

                case "1" -> {
                    System.out.println("Введите английское слово:");
                    String key = sc.next();

                    System.out.println("Введите превод:");

                    try {
                        dictionary.add(key, sc.next());
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case "2" -> {
                    System.out.println("Какое слово удалить (английское)?");
                    dictionary.remove(sc.next());
                }

                case "3" -> {
                    System.out.println("Введите английское слово для замены");
                    String key = sc.next();

                    System.out.println("Введите новый перевод:");

                    try {
                        dictionary.replace(key, sc.next());;
                    } catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case "4" -> System.out.println(dictionary.amount());

                case "5" -> System.out.println(dictionary.toString());

                case "6" -> {
                    System.out.println("Введите слово для поиска");

                    try {
                        dictionary.get(sc.next());
                    } catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                    }
                }

                default -> loop = false;
            }

            System.out.println("############################");

        }
    }

}
