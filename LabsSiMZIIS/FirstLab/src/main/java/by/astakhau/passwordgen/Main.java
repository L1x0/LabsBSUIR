package by.astakhau.passwordgen;

import by.astakhau.passwordgen.password.PasswordAnalyzer;
import by.astakhau.passwordgen.password.PasswordGeneration;

public class Main {
    public static void main(String[] args) {
        System.out.println(new PasswordGeneration().generatePassword(20));
        System.out.println(new PasswordAnalyzer(new PasswordGeneration()).symbolDistribution());

        new PasswordAnalyzer(new PasswordGeneration()).averageBrutTime();
    }
}