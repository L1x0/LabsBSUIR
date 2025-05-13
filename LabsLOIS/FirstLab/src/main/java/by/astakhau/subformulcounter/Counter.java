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
        countElements();
    }

    private String preprocessFormula(String input) {
        return input.replace("->", "<")
                .replace("\\/", "|")
                .replace("/\\", "&");
    }

    private void countElements() {
        operatorsCount = 0;
        variables.clear();

        for (char c : processedFormula.toCharArray()) {
            if (ALL_OPERATORS.contains(c)) {
                operatorsCount++;
            } else if (LITERALS.contains(c)) {
                variables.add(c);
            }
        }
    }

    public int getOperatorsCount() {
        return operatorsCount;
    }

    public int getUniqueVariablesCount() {
        return variables.size();
    }

    public int getAbsCount() {
        return operatorsCount + variables.size();
    }

    public Set<Character> getVariablesSet() {
        return new HashSet<>(variables);
    }
}