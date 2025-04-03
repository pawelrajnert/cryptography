package cryptoDESX;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DESTest {
    @Test
    @DisplayName("Klasa testowa do sprawdzania działania programu")
    void desTest() {
        DES des = new DES();
        String testMes = "abcdefgh";
        System.out.println("Wiadomość przed algorytmem: " + testMes + "|koniec wiadomości");

        des.setMessage(testMes.getBytes());
        System.out.println("Wiadomość w postaci bitowej: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");

        des.setMainKey();
        System.out.println("Klucz główny: " + Arrays.toString(des.getMainKey())  + "|koniec klucza");

        String shortMes = des.isMessageCorrect("abcd");
        System.out.println("Wiadomość za krotka po funkcji sprawdzajacej: " + shortMes+ "|koniec wiadomości");

        des.initialPermutation();
        System.out.println("Wiadomość po IP: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");

        des.messageAfterIPSplitter();
        System.out.println("Lewa część wiadomości: " + Arrays.toString(des.getLeftPart()) + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + Arrays.toString(des.getRightPart()) + "|koniec wiadomości");

        des.make56MainKey();
        System.out.println("Klucz w wersji 56 bit: "+ Arrays.toString(des.getMainKey())  + "|koniec klucza");
    }
}