package by.astakhau.passwordgen;

import by.astakhau.passwordgen.password.PasswordAnalyzer;
import by.astakhau.passwordgen.password.PasswordGeneration;

public class Main {
    public static void main(String[] args) {
        System.out.println("Пример генерации паролей:");
        PasswordGeneration passwordGeneration = new PasswordGeneration();
        for (int i = 0; i < 10; i++) {
            System.out.println(passwordGeneration.generatePassword(10));
        }

        PasswordAnalyzer passwordAnalyzer = new PasswordAnalyzer(passwordGeneration);

        System.out.println("Подбор пароля из 5 символов...");
        passwordAnalyzer.fullAnalyze();
    }
}