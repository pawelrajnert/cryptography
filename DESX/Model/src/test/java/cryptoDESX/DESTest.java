package cryptoDESX;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    Info:
    *   funkcja des.arrayToDecimal wyswietla liczbe w postaci dwójkowej, aby skorzystać z niej należy dopisać argument:
        version = "%8s" dla liczby 8 bitowej, "%7s" dla liczby 7 bitowej itp.
 */

class DESTest {
    @Test
    @DisplayName("Klasa testowa do sprawdzania działania programu")
    void desTest() throws UnsupportedEncodingException {
        DESX desx = new DESX();
        String shortMes = desx.isMessageCorrect("abcd");
        System.out.println("Wiadomość za krotka po funkcji sprawdzajacej: " + shortMes + "|koniec wiadomości");

        String testMes = "kryptoxd";
        byte[] testBytes = testMes.getBytes("ISO-8859-1");
        System.out.println("Wiadomość przed algorytmem: " + testMes + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci liczb ascii: " + Arrays.toString(testBytes) + "|koniec wiadomości");
        System.out.println("Wiadomość w postaci bitowej: " + desx.arrayToDecimal(testBytes,"%8s") + "|koniec wiadomości");

        desx.setMainKey("kDD3hVav");
        desx.setInitialKey("923jfds2");
        desx.setFinalKey("AKJSKHFd");

        System.out.println("Klucz główny: " + Arrays.toString(desx.getMainKey()) + "|koniec klucza");
        System.out.println("Klucz główny: " + desx.arrayToDecimal(desx.getMainKey(), "%8s") + "|koniec klucza");

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

        byte[] encrypted = desx.encryptMessage(testBytes, false);
        System.out.println("Wiadomość zakodowana powyższym kluczem: " + desx.arrayToDecimal(encrypted, "%8s"));
        byte[] decrypted = desx.encryptMessage(encrypted, true);
        System.out.println("Wiadomość odkodowana powyższym kluczem: " + desx.arrayToDecimal(decrypted, "%8s"));
        System.out.println("Początkowa wiadomość: " + desx.arrayToDecimal(testBytes, "%8s"));
        assertArrayEquals(testBytes, decrypted);

    }
}