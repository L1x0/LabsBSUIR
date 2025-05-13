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

import java.util.Scanner;

public class Test {
    private int score;
    Scanner scanner = new Scanner(System.in);

    public void start() {
        score = 0;

        System.out.println("Сколько из выражений являются корректными формулами языка логики высказываний?");
        System.out.println("(A(!B)");
        System.out.println("(1 /\\ (A /\\ B)");
        System.out.println(" /\\ (A /\\ B)");
        System.out.println("(A -> B)");
        System.out.println("A123");

        if (scanner.nextInt() == 2)
            score++;

        System.out.println("Сколько из выражений являются корректными формулами языка логики высказываний?");
        System.out.println("(!(B))");
        System.out.println("(B)");
        System.out.println("(A /\\ B)");
        System.out.println("(.L /\\ .L)");
        System.out.println("(A <- B)");

        if (scanner.nextInt() == 2)
            score++;

        System.out.println("Сколько из выражений являются корректными формулами языка логики высказываний?");
        System.out.println("(A(!B)");
        System.out.println("(A -> (Z0 -> B))");
        System.out.println("(A ~ (C -> D)");
        System.out.println("(A -> (B)");
        System.out.println("A0");

        if (scanner.nextInt() == 0)
            score++;

        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("(((((!P) /\\ Q) ~ R) \\/ ((S -> P) /\\ Q))");

        if (scanner.nextInt() == 10)
            score++;

        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("(R ~ (((!P) \\/ (Q /\\ R)) ~ (S -> (S -> P))))");

        if (scanner.nextInt() == 11)
            score++;

        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("((((P /\\ (!Q)) -> R) ~ P) \\/ (P /\\ Q))");

        if (scanner.nextInt() == 9)
            score++;

        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("(((P ~ Q) ~ ((((P /\\ R) /\\ P) \\/ (!P)) -> Q)) \\/ Q)");

        if (scanner.nextInt() == 11)
            score++;

        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("(!B)");

        if (scanner.nextInt() == 2)
            score++;


        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("(A /\\ (!B))");

        if (scanner.nextInt() == 4)
            score++;

        System.out.println("Сколько атомарных и неатомарных подформул в выражении?");
        System.out.println("A -> A");

        if (scanner.nextInt() == 2)
            score++;
    }

    public int getScore() {
        return score;
    }
}
