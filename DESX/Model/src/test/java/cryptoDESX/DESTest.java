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
    void desTest() throws UnsupportedEncodingException {
        DES des = new DES();
        DESX desx = new DESX(des);
        String testMes = "\u00ab\u00ab\u00ab\u00ab\u00ab\u00ab\u00ab\u00ab";
        des.setMessage(testMes);
        System.out.println("Wiadomość przed algorytmem: " + testMes + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci liczb ascii: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci bitowej: " + des.arrayToDecimal(des.getMessage(),"%8s") + "|koniec wiadomości");

        String testKey = "\u00fd\u0023\u00dc\u0011\u0058\u00ab\u00fe\u00b3";
        des.setMainKey(testKey);
        desx.setInitialKey(testKey);
        System.out.println("Klucz główny: " + Arrays.toString(desx.getInitialKey()) + "|koniec klucza");
        System.out.println("Klucz główny: " + des.arrayToDecimal(desx.getInitialKey(), "%8s") + "|koniec klucza");

        System.out.println("Klucz główny: " + Arrays.toString(des.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz główny: " + des.arrayToDecimal(des.getMainKey(), "%8s") + "|koniec klucza");

        String shortMes = des.isMessageCorrect("abcd");
        System.out.println("Wiadomość za krotka po funkcji sprawdzajacej: " + shortMes + "|koniec wiadomości");

        des.initialPermutation();
        System.out.println("Wiadomość po IP: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");
        System.out.println("Wiadomość po IP: " + des.arrayToDecimal(des.getMessage(),"%8s") + "|koniec wiadomości");

        des.messageAfterIPSplitter();
        byte[][] leftMesPart = des.getLeftMesParts();
        byte[][] rightMesPart = des.getRightMesParts();
        System.out.println("Lewa część wiadomości: " + Arrays.toString(leftMesPart[0]) + "|koniec wiadomości");
        System.out.println("Lewa część wiadomości: " + des.arrayToDecimal(leftMesPart[0], "%7s") + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + Arrays.toString(rightMesPart[0]) + "|koniec wiadomości");
        System.out.println("Prawa część wiadomości: " + des.arrayToDecimal(rightMesPart[0], "%8s") + "|koniec wiadomości");

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
        for (int i = 0; i < des.getRoundKeys().length; i++) {
            System.out.println("Runda " + (i + 1) + ": " + des.arrayToDecimal(des.getRoundKeys()[i], "%7s") + "|koniec klucza");
        }

        des.doPC2OnRoundKeys();
        System.out.println("Podklucze po PC2: " + Arrays.deepToString(des.getRoundKeys()) + "|koniec klucza");
        for (int j = 0; j < des.getRoundKeys().length; j++) {
            System.out.println("Runda " + (j + 1) + ": " + des.arrayToDecimal(des.getRoundKeys()[j], "%6s") + "|koniec klucza");
        }
        des.encryptMessage();
        System.out.println("Wiadomość zakodowana powyższym kluczem: " + des.arrayToDecimal(des.getFinalMessagePermutation(), "%8s"));

    }
}