package org.FirstLabPPOIS;

import java.util.NoSuchElementException;
import java.util.TreeMap;


public class Dictionary implements Comparable<Dictionary> {
    private TreeMap<String, String> dictionaryTree = new TreeMap<>();

    public Dictionary(Dictionary dictionary) {
        dictionaryTree = dictionary.getDictionary();
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

    public TreeMap<String, String> getDictionary() {
        return this.dictionaryTree;
    }

    @Override
    public int compareTo(Dictionary o) {
        return Integer.compare(dictionaryTree.size(), o.amount());
    }

    @Override
    public String toString() {
        return dictionaryTree.toString();
    }

    public String getAllKeys() {
        return dictionaryTree.keySet().toString();
    }

}
