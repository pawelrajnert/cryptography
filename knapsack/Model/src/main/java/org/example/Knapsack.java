package org.example;

import java.util.ArrayList;
import java.util.List;

public class Knapsack {
    private List<Integer> privateKey;
    private List<Integer> publicKey;
    private int n;
    private int m;

    public Knapsack() {
        generateKeys();
    }

    public void generateKeys() {
        privateKey = KeysGenerator.generatePrivateKey();
        m = KeysGenerator.generateM(privateKey);
        n = KeysGenerator.generateN(m);
        publicKey = KeysGenerator.generatePublicKey(m, n, privateKey);
    }

    /*
    Szyfrujemy bajt składający się z 8 bitów. Mechanizm uzyskiwania wyniku:
    - Sprawdzamy bit po bicie,
    - Jeśli bit wynosi 0, to nic nie robimy,
    - Jeśli bit wynosi 1 na pozycji n, to dodajemy do naszego wyniku wartość znajdującą się w kluczu publicznym na miejscu n,
    - Po sprawdzeniu wszystkich bitów, otrzymany wynik jest naszym zaszyfrowanym tekstem.
     */
    private int encryptSingleByte(byte singleByte) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            boolean currentBit = (singleByte >> (7 - i) & 1) == 1;
            if (currentBit) {
                result += publicKey.get(i);
            }
        }
        return result;
    }

    /*
    Wywołujemy szyfrowanie pojedynczego bajtu dla każdego bajtu z wiadomości.
     */
    public List<Integer> encrypt(byte[] bytes) {
        List<Integer> result = new ArrayList<>();
        for (byte b : bytes) {
            result.add(encryptSingleByte(b));
        }
        return result;
    }

    /*
    Deszyfrowanie tekstu. Mechanizm deszyfrowania:
    - Wyznaczamy n^-1. czyli takie n^-1, że (n * n^-1) mod m = 1
    - Następnie, dla zaszyfrowanego tekstu (Integer) obliczamy wynik wedle wzoru:
      wynik = (zaszyfrowany_tekst * n^-1) % m,
    - Otrzymany wynik zapisujemy jako sumę wartości z klucza prywatnego,
    - Na podstawie tego, czy dany element z klucza został wykorzystany, wyznaczamy końcowy odszyfrowany bajt:
        - jeśli wykorzystaliśmy i-ty element z klucza, to i-ty bit będzie wynosił 1,
        - jeśli nie wykorzystaliśmy i-tego elementu z klucza, to i-ty bit będzie wynosił 0.
    - Po zastosowaniu powyższej reguły dla każdego bitu otrzymujemy końcowy wynik deszyfrowania.
     */
    public byte[] decryptCipherText(List<Integer> cipherText) {
        List<Byte> decryptedList = new ArrayList<>();
        int nToMinusOne = 0;
        for (int i = 1; i <= m; i++) {
            if ((i * n) % m == 1) {
                nToMinusOne = i;
                break;
            }
        }   // podobno można szybciej ale nie chciało mi się xd
        for (Integer element : cipherText) {
            byte resultByte = 0;
            int result = (element * nToMinusOne) % m;
            for (int i = 0; i < 8; i++) {
                if (result >= privateKey.get(7 - i)) {
                    result -= privateKey.get(7 - i);
                    resultByte |= (byte) (1 << i);
                }
            }
            decryptedList.add(resultByte);
        }
        byte[] result = new byte[decryptedList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = decryptedList.get(i);
        }
        return result;
    }

    // skopiowałem z desx tymczasowo na potrzeby weryfikacji
    public static String arrayToDecimal(byte[] array, String version) {
        StringBuilder sb = new StringBuilder();
        for (byte a : array) {
            String binary = String.format(version, Integer.toBinaryString(a & 0xFF)).replace(' ', '0');
            sb.append(binary).append(" ");
        }
        return sb.toString();
    }

}
