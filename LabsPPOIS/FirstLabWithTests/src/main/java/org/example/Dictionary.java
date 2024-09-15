package org.example;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.Scanner;

public class Dictionary implements Comparable<Dictionary> {
    private TreeMap<String, String> dictionaryTree = new TreeMap<String, String>();

    public Dictionary(Dictionary dictionary) {
        dictionaryTree = dictionary.getDictionary();
    }

    public Dictionary(String filesName) {
        addFromFile(filesName);
    }

    public void addFromFile(String filesName) {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(filesName);

        assert ioStream != null;

        Scanner sc = new Scanner(ioStream);
        while (sc.hasNextLine()) {
            String[] str = sc.nextLine().split(" ");

            this.add(str[0], str[1]);

        }
    }


    public Dictionary(String key, String value) {
        this.add(key, value);
    }

    public Dictionary() {
    }

    public void add(String key, String value) {
        if (dictionaryTree.containsKey(key)) {
            throw new IllegalStateException("Ключ " + key + " уже существует");
        } else {
            dictionaryTree.put(key, value);
        }
    }

    public void remove(String key) {
        dictionaryTree.remove(key);
    }

    public String get(String key) {
        if (dictionaryTree.containsKey(key)) {
            System.out.println(key + " - " + dictionaryTree.get(key));

            return dictionaryTree.get(key);
        } else {
            throw new NoSuchElementException("Ключ " + key + " не найден в библиотеке");
        }
    }

    public void replace(String key, String newValue) {
        if (dictionaryTree.containsKey(key))
            dictionaryTree.replace(key, dictionaryTree.get(key), newValue);
        else
            throw new NoSuchElementException("Ключ " + key + " не найден в библиотеке");
    }

    public int amount() {
        return dictionaryTree.size();
    }

    public String returnAll() {
        return dictionaryTree.toString();
    }

    public TreeMap<String, String> getDictionary() {
        return this.dictionaryTree;
    }

    @Override
    public int compareTo(Dictionary o) {
        return Integer.compare(dictionaryTree.size(), o.amount());
    }

}
