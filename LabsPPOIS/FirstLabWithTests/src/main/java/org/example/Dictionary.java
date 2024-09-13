package org.example;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Scanner;

public class Dictionary implements Comparable<Dictionary> {
    private TreeMap<String, String> dictionaryTree = new TreeMap<String, String>();

    public Dictionary(Dictionary dictionary) {
        dictionaryTree = dictionary.getDictionary();
    }

    public Dictionary(String filesName) {

        try (Scanner sc = new Scanner(new File(filesName))) {
            while (sc.hasNextLine()) {
                String[] str = sc.nextLine().split(" ");

                dictionaryTree.put(str[0], str[1]);
            }
        } catch (IOException e) {
            System.out.println("Файл не найден");
        }
    }

    public Dictionary(String key, String value) {
        this.add(key, value);
    }

    public Dictionary() {}

    public void add(String key, String value) {
        if (dictionaryTree.containsKey(key)) {
            System.out.println("Это слово уже определено");
        } else {
            dictionaryTree.put(key, value);
        }
    }

    public void addFromConsole() {

        Scanner readFromConsole = new Scanner(System.in);

        System.out.println("Введите слово на английском, а затем чеерез пробел его переводы: ");

        while (readFromConsole.hasNextLine()) {
            String[] str = readFromConsole.nextLine().split(" ");

            this.add(str[0], str[1]);
        }
    }

    public void remove(String key) {
        dictionaryTree.remove(key);
    }

    public String get(String key) {
        System.out.println(key + " - " + dictionaryTree.get(key));

        return dictionaryTree.get(key);
    }

    public void replace(String key, String oldValue, String newValue) {
        dictionaryTree.replace(key, oldValue, newValue);
    }

    public int amount() {
        return dictionaryTree.size();
    }

    public void showAll() {
        System.out.println(dictionaryTree.toString());
    }

    public TreeMap<String, String> getDictionary() {
        return this.dictionaryTree;
    }

    @Override
    public int compareTo(Dictionary o) {
        return Integer.compare(dictionaryTree.size(), o.amount());
    }

}
