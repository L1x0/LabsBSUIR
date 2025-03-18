package by.astakhau.examresults.util;

import java.util.regex.Pattern;

public class Validator {
    private static final String FULL_NAME_REGEX = "^[A-Za-zА-Яа-яёЁ\\s-]+$";
    private static final String GROUP_NUMBER_REGEX = "^\\d{6}$";
    private static final String SUBJECT_NAME_REGEX = "^[A-Za-zА-Яа-яёЁ\\s-]+$";

    public static boolean validateFullName(String fullName) {
        return Pattern.matches(FULL_NAME_REGEX, fullName.trim());
    }

    public static boolean validateGroupNumber(String groupNumber) {
        return Pattern.matches(GROUP_NUMBER_REGEX, groupNumber.trim());
    }

    public static boolean validateSubjectName(String subjectName) {
        return Pattern.matches(SUBJECT_NAME_REGEX, subjectName.trim());
    }
}