/*
Лабораторная работа №1
Выполнил студент 321701 группы
Астахов Артём Сергеевич
Вариант 10

Исполняемый файл программы
04.05.2025

Источники:
-Учебно-методическое пособие по ЛОИС
-Шилдт, Герберт Java. Полное руководство. 10-е издание / Герберт Шилдт. – Москва: Компьютерное издательство "Диалектика", 2018. – 1500 с.
*/

package by.astakhau.subformulcounter;

import java.util.*;

public class Validation {
    private static final Set<Character> BINARY_OPERANDS = Set.of('&', '|', '<', '~');
    public static final Set<Character> ALL_OPERANDS = Set.of('&', '|', '<', '~', '!');
    private static final Set<Character> ARGUMENTS = Set.of(
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','0'
    );

    public static boolean validateInput(StringBuilder input) {
        if (!validateUnchanged(input.toString())) return false;
        if (input.toString().contains(" ")) return false;
        String converted = change(input.toString());
        input.setLength(0);
        input.append(converted);
        return validate2(input.toString());
    }

    private static boolean validateUnchanged(String input) {
        return !input.contains("&") && !input.contains("|") && !input.contains("<");
    }

    private static String change(String input) {
        return input.replace("->", "<")
                .replace("\\/", "|")
                .replace("/\\", "&");
    }

    private static boolean validate2(String input) {
        int depth = 0;
        int start = -1;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                if (depth == 0) start = i;
                depth++;
            }
            else if (input.charAt(i) == ')') {
                depth--;
                if (depth == 0 && start != -1) {
                    String sub = input.substring(start, i + 1);
                    if (!validate(sub)) return false;
                    start = -1;
                }
            }
        }
        return validate(input);
    }

    private static boolean validate(String input) {
        if (input.isEmpty()) return false;

        int open = 0, close = 0, ops = 0, binOps = 0, args = 0;

        for (char c : input.toCharArray()) {
            if (ALL_OPERANDS.contains(c)) ops++;
            if (BINARY_OPERANDS.contains(c)) binOps++;
            if (ARGUMENTS.contains(c)) args++;
            if (c == '(') open++;
            if (c == ')') close++;

            if (ops > open || close > open || args - binOps > 1)
                return false;
        }

        return open == close && open == ops;
    }
}