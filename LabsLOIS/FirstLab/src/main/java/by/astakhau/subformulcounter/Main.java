package by.astakhau.subformulcounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Введите формулу:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String formula = reader.readLine();
        boolean b = Validation.validateInput(new StringBuilder(formula));

        if (b) {
            System.out.println("is correct");
            System.out.println(new Counter(formula).getAbsCount());
        } else {
            System.out.println("is incorrect");
        }
    }
}