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

import java.util.*;

public class Counter {
    private static final Set<Character> LITERALS = Set.of(
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    );
    private static final Set<Character> ALL_OPERATORS = Set.of('&', '|', '<', '~', '!');

    private final String processedFormula;
    private int operatorsCount;
    private final Set<Character> variables = new HashSet<>();

    public Counter(String formula) {
        this.processedFormula = preprocessFormula(formula);
        getAbsCount();
    }

    private String preprocessFormula(String input) {
        return input.replace("->", "<")
                .replace("\\/", "|")
                .replace("/\\", "&");
    }

    public int getAbsCount() {
        operatorsCount = 0;
        variables.clear();

        for (char c : processedFormula.toCharArray()) {
            if (ALL_OPERATORS.contains(c)) {
                operatorsCount++;
            } else if (LITERALS.contains(c)) {
                variables.add(c);
            }
        }

        return operatorsCount + variables.size();
    }
}