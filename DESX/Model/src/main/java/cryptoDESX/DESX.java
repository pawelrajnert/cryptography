package cryptoDESX;

import java.io.UnsupportedEncodingException;

public class DESX extends DES {
    private byte[] initialKey;
    private byte[] finalKey;

    // klucze ustawiamy podobnie jak w oryginalnym DESie
    public void setInitialKey(String initialKey) {
        try {
            this.initialKey = isKeyCorrect(initialKey).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setFinalKey(String finalKey) {
        try {
            this.finalKey = isKeyCorrect(finalKey).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getInitialKey() {
        return initialKey;
    }

    public byte[] getFinalKey() {
        return finalKey;
    }

    /*
    Operacje XOR kluczy z wiadomością wykonywane przed oraz po całym algorytmie.
     */
    private byte[] firstEncrypt(byte[] message) {
        for (int i = 0; i < 8; i++) {
            message[i] = (byte) (initialKey[i] ^ message[i]);
        }
        return message;
    }

    private byte[] finalEncrypt(byte[] message) {
        for (int i = 0; i < 8; i++) {
            message[i] = (byte) (finalKey[i] ^ message[i]);
        }
        return message;
    }

    /*
    Analogicznie jak w przypadku DESa, komentarze w kodzie metody poniższej odnoszą się do instrukcji z dołu
     */

    @Override
    public byte[] encryptMessage(byte[] message, boolean isEncrypt) {
        byte[] result = new byte[8];
        System.arraycopy(message, 0, result, 0, 8);
        System.out.println("\nDESX encryptMessage");
        System.out.println("Initial key: " + arrayToDecimal(initialKey, "%8s"));
        System.out.println("Final key: " + arrayToDecimal(finalKey, "%8s"));
        System.out.println("DES key: " + arrayToDecimal(getMainKey(), "%8s"));
        if (isEncrypt) {        // deszyfrujemy wiadomość
            System.out.println("DESX decrypt is called");
            System.out.println("Initial message: " + arrayToDecimal(result, "%8s"));
            result = finalEncrypt(result);              // 3)
            System.out.println("After first encrypt: " + arrayToDecimal(result, "%8s"));
            result = super.encryptMessage(result, isEncrypt);   // 2)
            System.out.println("DES has been called, result: " + arrayToDecimal(result, "%8s"));
            result = firstEncrypt(result);                  // 1)
        }
        else {              // szyfrujemy wiadomość
            System.out.println("DESX encrypt is called");
            System.out.println("Initial message: " + arrayToDecimal(result, "%8s"));
            result = firstEncrypt(result);          // 1)
            System.out.println("After first encrypt: " + arrayToDecimal(result, "%8s"));
            result = super.encryptMessage(result, isEncrypt);       // 2)
            System.out.println("DES has been called, result: " + arrayToDecimal(result, "%8s"));
            result = finalEncrypt(result);                  // 3)
        }
        return result;
    }
}


/*
DESX to pewnego rodzaju ulepszenie algorytmu DES. Różni się tym, że posiada on dodatkowe dwa klucze 64-bitowe.
Procedura zaszyfrowania wiadomości wygląda następująco:
1) Wykonaj operacje XOR pierwszego klucza z wiadomością,
2) Wykonaj wszystkie operacje z DESa,
3) Wykonaj operacje XOR otrzymanej zaszyfrowanej wiadomości z drugim dodatkowym kluczem.
W ten sposób zdecydowanie trudniej jest złamać zaszyfrowane dane komuś, jeśli nie zna kluczy.

Procedura deszyfrowania wiadomości również jest praktycznie taka sama,
różnica polega na tym, że operacje wykonujemy w kolejności 3) - 2) - 1)
 */

