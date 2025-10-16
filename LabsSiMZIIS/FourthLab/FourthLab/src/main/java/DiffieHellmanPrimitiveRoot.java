import java.security.SecureRandom;
import java.util.Scanner;

public class DiffieHellmanPrimitiveRoot {
    static final int P = 5717;

    public static void main(String[] args) {
        System.out.println("Поиск примитивного корня для p = " + P + " (перебор)...");
        int g = findPrimitiveRoot(P);
        if (g == -1) {
            System.out.println("Примитивный корень не найден (это странно для простого p).\n");
            return;
        }
        System.out.println("Найден примитивный элемент g = " + g + "\n");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Вы можете ввести приватные числа a и b (целые >1 и <p-1) через Enter.\n" +
                "Если оставите строку пустой, будут сгенерированы случайные приватные ключи.");

        Integer a = null, b = null;
        try {
            System.out.print("Приватный ключ a (или Enter): ");
            String sa = scanner.nextLine().trim();
            if (!sa.isEmpty()) a = Integer.parseInt(sa);

            System.out.print("Приватный ключ b (или Enter): ");
            String sb = scanner.nextLine().trim();
            if (!sb.isEmpty()) b = Integer.parseInt(sb);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа. Использую случайные ключи.");
            a = null; b = null;
        }

        SecureRandom rnd = new SecureRandom();
        if (a == null) a = 2 + rnd.nextInt(P - 3);
        if (b == null) b = 2 + rnd.nextInt(P - 3);

        System.out.println("Используем приватные a=" + a + ", b=" + b);

        long A = modPow(g, a, P);
        long B = modPow(g, b, P);
        System.out.println("Открытые значения: A = g^a mod p = " + A + ", B = g^b mod p = " + B);

        long shared1 = modPow(B, a, P);
        long shared2 = modPow(A, b, P);

        System.out.println("Общий секрет, вычисленный как B^a mod p = " + shared1);
        System.out.println("Общий секрет, вычисленный как A^b mod p = " + shared2);
        System.out.println("Секреты совпадают: " + (shared1 == shared2));

        scanner.close();
    }

    static long modPow(long base, long exp, long mod) {
        long result = 1;
        long b = base % mod;
        long e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = (result * b) % mod;
            }
            b = (b * b) % mod;
            e >>= 1;
        }
        return result;
    }

    static int findPrimitiveRoot(int p) {
        for (int g = 2; g <= p - 1; g++) {
            if (isPrimitiveRootBruteforce(g, p)) return g;
        }
        return -1;
    }

    static boolean isPrimitiveRootBruteforce(int g, int p) {
        boolean[] seen = new boolean[p];
        int count = 0;
        for (int k = 1; k <= p - 1; k++) {
            long v = modPow(g, k, p);
            if (v == 0) return false;
            if (seen[(int) v]) {
                return false;
            }
            seen[(int) v] = true;
            count++;
        }

        if (count != p - 1) return false;
        for (int i = 1; i <= p - 1; i++) {
            if (!seen[i]) return false;
        }
        return true;
    }
}
