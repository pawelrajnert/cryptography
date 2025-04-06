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
        String testMes = "\u0001\u0023\u0045\u0067\u0089\u00ab\u00cd\u00ef";
        byte[] bytes = testMes.getBytes("ISO-8859-1");

        System.out.println("Wiadomość przed algorytmem: " + testMes + "|koniec wiadomości");

        des.setMessage(bytes);
        System.out.println("Wiadomość w postaci liczb ascii: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci bitowej: " + des.arrayToDecimal(des.getMessage(),"%8s") + "|koniec wiadomości");

        String testKey = "\u0001\u0023\u0045\u0067\u0089\u00ab\u00cd\u00ef";
        bytes = testKey.getBytes("ISO-8859-1");

        des.setMainKey(bytes);

        System.out.println("Klucz główny: " + Arrays.toString(des.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz główny: " + des.arrayToDecimal(des.getMainKey(), "%8s") + "|koniec klucza");

        String shortMes = des.isMessageCorrect("abcd");
        System.out.println("Wiadomość za krotka po funkcji sprawdzajacej: " + shortMes + "|koniec wiadomości");

        des.initialPermutation();
        System.out.println("Wiadomość po IP: " + Arrays.toString(des.getMessage()) + "|koniec wiadomości");
        System.out.println("Wiadomość po IP: " + des.arrayToDecimal(des.getMessage(),"%8s") + "|koniec wiadomości");

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
        for (int i = 0; i < des.getRoundKeys().length; i++) {
            System.out.println("Runda " + (i + 1) + ": " + des.arrayToDecimal(des.getRoundKeys()[i], "%7s") + "|koniec klucza");
        }

        des.doPC2OnRoundKeys();
        System.out.println("Podklucze po PC2: " + Arrays.deepToString(des.getRoundKeys()) + "|koniec klucza");
        for (int j = 0; j < des.getRoundKeys().length; j++) {
            System.out.println("Runda " + (j + 1) + ": " + des.arrayToDecimal(des.getRoundKeys()[j], "%6s") + "|koniec klucza");
        }
        byte[] ebit = des.expandMessageWithEbitTable();
        System.out.println(des.arrayToDecimal(ebit, "%6s"));
        byte[] k = des.kOperation(ebit);
        System.out.println(des.arrayToDecimal(k, "%6s"));
        byte[] s = des.sBoxOperation(k);
        System.out.println(des.arrayToDecimal(s, "%4s"));
        byte[] f = des.fFunction(s);
        System.out.println(des.arrayToDecimal(f, "%4s"));

    }
}