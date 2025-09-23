package by.astakhau.crypto.cipher;

public class Caesar {
    public static String encrypt(String plainText, int key) {
        char[] charArray = plainText.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            int newIndex = (((int) charArray[i] + key - 96) % 26);
            charArray[i] = (char) (newIndex + 96);
        }

        return new String(charArray);
    }

    public static void decrypt(String cipherText, String answer) {
        System.out.println("question: " + cipherText);
        System.out.println("Answer: " + answer);

        for (int i = 0; ; i++) {
            char[] charArray = cipherText.toCharArray();

            for (int j = 0; j < charArray.length; j++) {
                int newIndex = ((int) charArray[j]) - 96 - i;

                if (newIndex <= 0) {
                    charArray[j] = (char) (122 + newIndex);
                } else {
                    charArray[j] = (char) (96 + newIndex);
                }
            }
            System.out.println("Тестируется ключ: " + i);

            if (answer.equals(new String(charArray))) {
                System.out.println("Ключ -- " + i);
                break;
            }
        }
    }
}
