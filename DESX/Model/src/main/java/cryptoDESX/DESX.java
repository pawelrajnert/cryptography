package cryptoDESX;

import java.io.UnsupportedEncodingException;

/*
DESX to pewnego rodzaju ulepszenie algorytmu DES. Różni się tym, że posiada on dodatkowe dwa klucze 64-bitowe.
Procedura zaszyfrowania wiadomości wygląda następująco:
1) Wykonaj operacje XOR pierwszego klucza z wiadomością,
2) Wykonaj wszystkie operacje z DESa,
3) Wykonaj operacje XOR otrzymanej zaszyfrowanej wiadomości z drugim dodatkowym kluczem.
W ten sposób zdecydowanie trudniej jest złamać zaszyfrowane dane komuś, jeśli nie zna kluczy.
 */

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
    Operacje XOR wykonywane przed oraz po całym algorytmie.
    W przypadku deszyfrowania, operacje te wykonujemy w odwrotnej kolejności
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

    @Override
    public byte[] encryptMessage(byte[] message, boolean isEncrypt) {
        byte[] result = new byte[8];
        System.arraycopy(message, 0, result, 0, 8);
        System.out.println("DESX encryptMessage");
        System.out.println("Initial key: " + arrayToDecimal(initialKey, "%8s"));
        System.out.println("Final key: " + arrayToDecimal(finalKey, "%8s"));
        System.out.println("DES key: " + arrayToDecimal(getMainKey(), "%8s"));
        if (isEncrypt) {
            System.out.println("DESX decrypt is called");
            System.out.println("Initial message: " + arrayToDecimal(result, "%8s"));
            result = finalEncrypt(result);
            System.out.println("After first encrypt: " + arrayToDecimal(result, "%8s"));
            result = super.encryptMessage(result, isEncrypt);
            System.out.println("DES has been called, result: " + arrayToDecimal(result, "%8s"));
            result = firstEncrypt(result);
        }
        else {
            System.out.println("DESX encrypt is called");
            System.out.println("Initial message: " + arrayToDecimal(result, "%8s"));
            result = firstEncrypt(result);
            System.out.println("After first encrypt: " + arrayToDecimal(result, "%8s"));
            result = super.encryptMessage(result, isEncrypt);
            System.out.println("DES has been called, result: " + arrayToDecimal(result, "%8s"));
            result = finalEncrypt(result);
        }
        return result;
    }
}


