package by.astakhau.crypto;

import by.astakhau.crypto.cipher.Caesar;

public class Main {
    public static void main(String[] args) {

        System.out.println("encrypt: abcdefgz => " + Caesar.encrypt("abcdefgz", 3));
        Caesar.decrypt("defghijc", "abcdefgz");
    }
}