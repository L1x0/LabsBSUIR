package by.astakhau.passwordgen.password;

import java.security.SecureRandom;

public class PasswordGeneration {
    private final int ASCII_a = 97;
    private final int ASCII_z = 122;


    public String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append((char)random.nextInt(ASCII_a, ASCII_z + 1));
        }

        return password.toString();
    }
}
