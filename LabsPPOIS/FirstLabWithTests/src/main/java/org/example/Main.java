package org.example;

public class Main {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary("src/input.txt");
        dictionary.showAll();
        Dictionary d = new Dictionary("1", "2");
        if (dictionary.compareTo(d) > 0) {
            System.out.println("greater");
        }
    }
}