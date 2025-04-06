package cryptoDESX;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/*
    Info:
    *   funkcja des.arrayToDecimal wyswietla liczbe w postaci dwójkowej, aby skorzystać z niej należy dopisać argument:
        version = "%8s" dla liczby 8 bitowej, "%7s" dla liczby 7 bitowej itp.
 */

class DESTest {
    @Test
    @DisplayName("Klasa testowa do sprawdzania działania programu")
    void desTest() {
        DES des = new DES();
        String testMes = "abcdefgh";
        System.out.println("Wiadomość przed algorytmem: " + testMes + "|koniec wiadomości");

        des.setMessage(testMes.getBytes());
        System.out.println("Wiadomość w postaci bitowej: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");

        String testKey = "krypto11";
        des.setMainKey(testKey.getBytes());
        System.out.println("Klucz główny: " + Arrays.toString(des.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz główny: " + des.arrayToDecimal(des.getMainKey(), "%8s") + "|koniec klucza");

        String shortMes = des.isMessageCorrect("abcd");
        System.out.println("Wiadomość za krotka po funkcji sprawdzajacej: " + shortMes + "|koniec wiadomości");

        des.initialPermutation();
        System.out.println("Wiadomość po IP: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");

        des.messageAfterIPSplitter();
        System.out.println("Lewa część wiadomości: " + Arrays.toString(des.getLeftMesPart()) + "|koniec wiadomości");
        System.out.println("Lewa część wiadomości: " + des.arrayToDecimal(des.getLeftMesPart(), "%7s") + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + Arrays.toString(des.getRightMesPart()) + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + des.arrayToDecimal(des.getRightMesPart(), "%8s") + "|koniec wiadomości");

        des.doPC1on56bitKey();
        System.out.println("Klucz w wersji 56 bit po PC1: " + Arrays.toString(des.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz w wersji 56 bit po PC1: " + des.arrayToDecimal(des.getMainKey(), "%7s") + "|koniec klucza");

        des.mainKey56bitSplitter();
        System.out.println("Klucz w wersji 28 bit (lewa część): " + Arrays.toString(des.getLeftKeyPart()) + "|koniec klucza");
        System.out.println("Klucz w wersji 28 bit (lewa część): " + des.arrayToDecimal(des.getLeftKeyPart(), "%7s") + "|koniec klucza");
        System.out.println("Klucz w wersji 28 bit (prawa część): " + Arrays.toString(des.getRightKeyPart()) + "|koniec klucza");
        System.out.println("Klucz w wersji 28 bit (prawa część): " + des.arrayToDecimal(des.getRightKeyPart(), "%7s") + "|koniec klucza");

        des.makeRoundKeys();
        System.out.println("Złączone podklucze po rotacji w lewo (16 rund): " + Arrays.deepToString(des.getRoundKeys()) + "|koniec klucza");

        des.doPC2OnRoundKeys();
        System.out.println("Podklucze po PC2: " + Arrays.deepToString(des.getRoundKeys()) + "|koniec klucza");
    }
}