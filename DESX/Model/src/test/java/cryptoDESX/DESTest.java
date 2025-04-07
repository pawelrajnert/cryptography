package cryptoDESX;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
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
        DESX desx = new DESX();
        String shortMes = desx.isMessageCorrect("abcd");
        System.out.println("Wiadomość za krotka po funkcji sprawdzajacej: " + shortMes + "|koniec wiadomości");

        String testMes = "\u00ab\u00ab\u00ab\u00ab\u00ab\u00ab\u00ab\u00ab";
        desx.setMessage(testMes);
        System.out.println("Wiadomość przed algorytmem: " + testMes + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci liczb ascii: " + Arrays.toString(desx.getMessage()) + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci bitowej: " + desx.arrayToDecimal(desx.getMessage(),"%8s") + "|koniec wiadomości");

        String testKey = "\u00fd\u0023\u00dc\u0011\u0058\u00ab\u00fe\u00b3";
        desx.setMainKey(testKey);
        desx.setInitialKey("923jfds2");
        desx.setFinalKey("AKJSKHFd");

        System.out.println("Klucz główny: " + Arrays.toString(desx.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz główny: " + desx.arrayToDecimal(desx.getMainKey(), "%8s") + "|koniec klucza");

        desx.initialPermutation();
        System.out.println("Wiadomość po IP: " + Arrays.toString(desx.getMessage()) + "|koniec wiadomości");
        System.out.println("Wiadomość po IP: " + desx.arrayToDecimal(desx.getMessage(),"%8s") + "|koniec wiadomości");

        desx.messageAfterIPSplitter();
        byte[][] leftMesPart = desx.getLeftMesParts();
        byte[][] rightMesPart = desx.getRightMesParts();
        System.out.println("Lewa część wiadomości: " + Arrays.toString(leftMesPart[0]) + "|koniec wiadomości");
        System.out.println("Lewa część wiadomości: " + desx.arrayToDecimal(leftMesPart[0], "%7s") + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + Arrays.toString(rightMesPart[0]) + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + desx.arrayToDecimal(rightMesPart[0], "%8s") + "|koniec wiadomości");

        System.out.println("Klucz w wersji 56 bit po PC1: " + Arrays.toString(desx.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz w wersji 56 bit po PC1: " + desx.arrayToDecimal(desx.getMainKey(), "%7s") + "|koniec klucza");

        System.out.println("Klucz w wersji 28 bit (lewa część): " + Arrays.toString(desx.getLeftKeyPart()) + "|koniec klucza");
        System.out.println("Klucz w wersji 28 bit (lewa część): " + desx.arrayToDecimal(desx.getLeftKeyPart(), "%7s") + "|koniec klucza");
        System.out.println("Klucz w wersji 28 bit (prawa część): " + Arrays.toString(desx.getRightKeyPart()) + "|koniec klucza");
        System.out.println("Klucz w wersji 28 bit (prawa część): " + desx.arrayToDecimal(desx.getRightKeyPart(), "%7s") + "|koniec klucza");

        System.out.println("Złączone podklucze po rotacji w lewo (16 rund): " + Arrays.deepToString(desx.getRoundKeys()) + "|koniec klucza");
        for (int i = 0; i < desx.getRoundKeys().length; i++) {
            System.out.println("Runda " + (i + 1) + ": " + desx.arrayToDecimal(desx.getRoundKeys()[i], "%7s") + "|koniec klucza");
        }

        System.out.println("Podklucze po PC2: " + Arrays.deepToString(desx.getRoundKeys()) + "|koniec klucza");
        for (int j = 0; j < desx.getRoundKeys().length; j++) {
            System.out.println("Runda " + (j + 1) + ": " + desx.arrayToDecimal(desx.getRoundKeys()[j], "%6s") + "|koniec klucza");
        }
        desx.encryptMessage();
        System.out.println("Wiadomość zakodowana powyższym kluczem: " + desx.arrayToDecimal(desx.getFinalMessagePermutation(), "%8s"));

    }
}