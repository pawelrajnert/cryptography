package cryptoDESX;

import java.io.UnsupportedEncodingException;

public class DES {
    private byte[] mainKey;
    private byte[] leftKeyPart = new byte[4];
    private byte[] rightKeyPart = new byte[4];
    private final SBox sBox;
    private byte[] message;
    private final Permutation permutation;
    private byte[] leftMesPart = new byte[4];
    private byte[] rightMesPart = new byte[4];
    private byte[][] roundKeys = new byte[16][8];


    DES() {
        this.sBox = new SBox();
        this.permutation = new Permutation();
    }

    public byte[] getMainKey() {
        return mainKey;
    }

    public byte[] getMessage() {
        return message;
    }

    public byte[] getLeftKeyPart() {
        return leftKeyPart;
    }

    public byte[] getRightKeyPart() {
        return rightKeyPart;
    }

    public byte[][] getRoundKeys() {
        return roundKeys;
    }

    // funkcja sprawdza czy wprowadzona wiadomość jest prawidłowego rozmiaru (8 bit lub wieloktotnosc)
    // jesli jej dlugosc jest inna to funkcja dopisuje spacje do wiadomosci
    public String isMessageCorrect(String message) {
        int length = message.length();
        if ((length % 8) != 0) {
            for (int i = 0; i < 8 - (length % 8); i++) {
                message += " ";
            }
        }
        return message;
    }

    // ustawiamy poprawny rozmiar wiadomości
    public void setMessage(byte[] message) {
        try {
            String messageString = new String(message, "ISO-8859-1");
            messageString = isMessageCorrect(messageString);
            this.message = messageString.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // sprawdzamy czy klucz ma 8 bajtow, a jak nie to dopisujemy do niego spacje
    public String isKeyCorrect(String key) {
        if (key == null || key.length() == 0 || key.length() > 8) {
            return "abcdefgh"; // jesli klucz jest niepoprawny, to ustawiamy narazie taki domyślny
        }
        while (key.length() < 8) {
            key += " ";
        }

        return key;
    }

    // ustawiamy klucz jesli wprowadzony jest poprawny
    public void setMainKey(byte[] key) {
        try {
            String keyString = new String(key, "ISO-8859-1");
            keyString = isKeyCorrect(keyString);
            this.mainKey = keyString.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getLeftMesPart() {
        return leftMesPart;
    }

    public byte[] getRightMesPart() {
        return rightMesPart;
    }

    public static String arrayToDecimal(byte[] array, String version) {
        StringBuilder sb = new StringBuilder();
        for (byte a : array) {
            String binary = String.format(version, Integer.toBinaryString(a & 0xFF)).replace(' ', '0');
            sb.append(binary).append(" ");
        }
        return sb.toString();
    }

    // krok 1, wiadomosc jest poddana initial permutation
    public void initialPermutation() {
        byte[] IPmessage = new byte[message.length];
        for (int i = 0; i < 8; i++) { // bo tablica IP jest 8 x 8
            for (int j = 0; j < 8; j++) {
                int bitIndex = permutation.IP[i][j] - 1; // pobieramy numer bitu
                int byteIndex = bitIndex / 8; // informacja o tym w ktorym bajcie jest nasz obecnie rozwazany bit
                int bitPossition = bitIndex % 8; // numer bitu w wyzej sprawdzanym bajcie

                // odczytujemy wartosc bitu
                boolean currentBit = ((message[byteIndex] >> (7 - bitPossition)) & 1) == 1;

                int outBitIndex = i * 8 + j; // po permutacji tutaj ma znalezc sie rozwazany bit
                int outByteIndex = outBitIndex / 8; // informacja o bajcie
                int outBytePossition = outBitIndex % 8; // informacja o bicie w bajcie

                if (currentBit == true) { // jesli rozwazany bit to 1 to wrzucamy go do IPmessage
                    IPmessage[outByteIndex] |= (1 << (7 - outBytePossition));
                } else { // jesli rozwazany bit to 0 to wrzucamy 0 do IPmessage
                    IPmessage[outByteIndex] &= ~(1 << (7 - outBytePossition));
                }
            }
        }
        message = IPmessage;
    }

    // dzielimy wiadomosc po initial permutation na 2 czesci- lewa i prawa (64 bity -> 2x 32bity)
    public void messageAfterIPSplitter() {
        if (message.length != 8 || message == null) {
            int pom = message.length;
            throw new IllegalArgumentException("Wiadomość nie ma 8 bajtów, tylko ma ich: " + pom);
        }

        for (int i = 0; i < 4; i++) {
            leftMesPart[i] = message[i]; // lewa czesc to bajty: 0, 1, 2, 3 (32 bity)
            rightMesPart[i] = message[i + 4]; // prawa czesc to bajty: 4, 5, 6, 7 (32 bity)
        }
    }

    // klucz 64 bitowy poddajemy PC1, dzięki czemu usuniemy też bity parzystości i klucz będzie 56 bit
    public void doPC1on56bitKey() {
        if (mainKey.length != 8 || mainKey == null) {
            int pom = mainKey.length;
            throw new IllegalArgumentException("Klucz nie ma 8 bajtów, tylko ma ich: " + pom);
        }

        byte[] pc1OnKey = new byte[8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                int bitIndex = permutation.PC1[i][j] - 1; // pobieramy numer bitu (-1, bo indeksujemy od 0)
                // jest to bit docelowy poniewaz permutacja PC1 mowi nam gdzie na ktore miejsce ma trafic bit z klucza np.
                // np. 1 wartosc w macierzy PC1 to 57, zatem 1 bit z naszego klucza ma trafic na miejsce 57

                int byteIndex = bitIndex / 8; // informacja o tym w ktorym bajcie jest nasz obecnie rozwazany bit
                int bitPossition = 7 - (bitIndex % 8); // numer bitu w wyzej sprawdzanym bajcie

                // odczytujemy wartosc bitu
                boolean currentBit = ((mainKey[byteIndex] >> bitPossition) & 1) == 1;

                int outBitIndex = 6 - j;

                if (currentBit == true) {
                    pc1OnKey[i] |= (1 << outBitIndex);
                } else {
                    pc1OnKey[i] &= ~(1 << outBitIndex);
                }
            }
        }
        mainKey = pc1OnKey;
    }

    // dzielimy 56 bitowy klucz na 2 x 28 bit podklucze
    public void mainKey56bitSplitter() {
        // klucz powinien miec 56 bitow ALE ZAPISUJEMY TO NA 8 BAJTACH I IGNORUJEMY BIT PARZYSTOŚCI, dlatego sprawdzamy czy ma 8 bajtow
        if (mainKey.length != 8 || mainKey == null) {
            int pom = mainKey.length;
            throw new IllegalArgumentException("Klucz nie ma 8 bajtów, tylko ma ich: " + pom);
        }

        byte[] left = new byte[4];
        byte[] right = new byte[4];

        for (int i = 0; i < 28; i++) { // dodajemy pierwsze 28 bit do lewej części klucza
            int byteIndex = i / 7; // rozwazany bajt (patrzymy gdzie trafi bit, 7 bitow w bajcie rozwazamy)
            int bitPossition = 6 - (i % 7); // pozycja bitu w bajcie

            // odczytujemy wartosc bitu
            boolean currentBit = ((mainKey[byteIndex] >> bitPossition) & 1) == 1;

            if (currentBit == true) { // jesli bit to 1 to tak go ustawiamy
                left[byteIndex] |= (1 << bitPossition);
            } else { // jesli bit to 0 to zostawiamy go jako 0
                left[byteIndex] &= ~(1 << bitPossition);
            }
        }
        for (int j = 28; j < 56; j++) { // dodajemy drugie 28 bit do prawej części klucza
            int byteIndex = (j - 28) / 7; // rozwazany bajt w prawej części klucza
            int bitPossition = 6 - ((j - 28) % 7); // pozycja bitu w bajcie

            // odczytujemy wartosc bitu
            boolean currentBit = ((mainKey[j / 7] >> bitPossition) & 1) == 1;

            if (currentBit == true) { // jesli bit to 1 to tak go ustawiamy
                right[byteIndex] |= (1 << bitPossition);
            } else { // jesli bit to 0 to zostawiamy go jako 0
                right[byteIndex] &= ~(1 << bitPossition);
            }
        }
        this.leftKeyPart = left;
        this.rightKeyPart = right;
    }

    // tworzymy 16 kluczy dla kazdej z rund
    public void makeRoundKeys() {
        if (leftKeyPart.length != 4 || rightKeyPart.length != 4) {
            int pomL = leftKeyPart.length;
            int pomR = rightKeyPart.length;
            throw new IllegalArgumentException("Podklucze nie mają po 4 bajty (28 bit zapisujemy, ale potrzebujemy 32 do utworzenia pełnego bajta), tylko mają kolejno, lewy: " + pomL + " prawy: " + pomR);
        }

        final int[] shiftsReader = Permutation.getShifts();

        for (int i = 0; i < 16; i++) {
            rotateLeftSingleKey(leftKeyPart, shiftsReader[i]); // rotujemy o daną liczbę lewą część klucza w zależności od rundy
            rotateLeftSingleKey(rightKeyPart, shiftsReader[i]); // rotujemy o daną liczbę prawą część klucza w zależności od rundy

            // tak tworzymy daną część podklucza, przesuwamy o odpowiednią liczbę bitów w zależności od bajtu i maskujemy maską 7 bitową (127 = 01111111)
            // w taki sposób zapiszemy wynik na 4 bajtach, ale będzie interesowało nas tylko 7 bit z każdego bajtu
            // mozemy dzialac na incie bo w javie 64 bitowy int jest na 4 bajtach (rozmiar podklucza)
            int leftPart = ((leftKeyPart[0] & 127) << 21) | ((leftKeyPart[1] & 127) << 14) | ((leftKeyPart[2] & 127) << 7) | (leftKeyPart[3] & 127);

            int rightPart = ((rightKeyPart[0] & 127) << 21) | ((rightKeyPart[1] & 127) << 14) | ((rightKeyPart[2] & 127) << 7) | (rightKeyPart[3] & 127);

            // przechodzimy na long, bo w javie 64 bitowy long to 8 bajtow
            long fullKey = (((long) leftPart) << 28) | ((rightPart) & 0x0FFFFFFF); // maska 28 bit (0x0FFFFFFF) zeby tylko tyle brac pod uwage

            // przepisujemy wartosci do naszej tablicy z kluczami rundowymi, łącznie mamy 8 bajtow, ale w kazdym po 7 bit nas interesuje
            for (int j = 0; j < 8; j++) {
                roundKeys[i][j] = (byte) ((fullKey >>> (7 * (7 - j))) & 127);
            }
        }
    }

    public void rotateLeftSingleKey(byte[] key, int shiftAmount) {
        // wczytujemy nasza liczbe z tablicy bajtow do inta (mozemy dzialac na incie bo w javie int 64 bitowy jest na 4 bajtach)
        int keyPart = ((key[0] & 127) << 21) | ((key[1] & 127) << 14) | ((key[2] & 127) << 7) | (key[3] & 127);

        // rotujemy w lewo o podaną ilość i przesuwamy bit najbardziej po lewo na sam koniec + na koniec maska na 28 bit
        keyPart = ((keyPart << shiftAmount) | (keyPart >>> (28 - shiftAmount))) & 0x0FFFFFFF;

        // wracamy znowu do tablicy bajtow, biorąc pod uwagę 4 bajty, ale 7 bit nas interesuje tylko dlatego maska 127
        key[0] = (byte) ((keyPart >>> 21) & 127);
        key[1] = (byte) ((keyPart >>> 14) & 127);
        key[2] = (byte) ((keyPart >>> 7) & 127);
        key[3] = (byte) (keyPart & 127);
    }

    public void doPC2OnRoundKeys() { // permutacja działa jak PC1, tylko tym razem robimy z 56 bitów 48 (DALEJ ZAPISUJEMY TO NA 8 BAJTACH, ALE KORZYSTAMY TYLKO Z 6 BITOW NA BAJT)
        for (int roundKey = 0; roundKey < 16; roundKey++) {
            if (roundKeys[roundKey] == null || roundKeys[roundKey].length != 8) {
                int pom = roundKeys[roundKey].length;
                throw new IllegalArgumentException("Klucz rundowy na wejściu nie ma 8 bajtów, tylko ma ich: " + pom);
            }

            byte[] pc2OnKey = new byte[6];
            int pom1 = 0;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 6; j++) {
                    int bitIndex = permutation.PC2[i][j] - 1;
                    int byteIndex = bitIndex / 8;
                    int bitPosition = 7 - (bitIndex % 8);

                    boolean currentBit = (((roundKeys[roundKey])[byteIndex] >> bitPosition) & 1) == 1;

                    int outByteIndex = pom1 / 8;
                    int outBitIndex = 7 - (pom1 % 8);

                    if (currentBit) {
                        pc2OnKey[outByteIndex] |= (1 << outBitIndex);
                    } else {
                        pc2OnKey[outByteIndex] &= ~(1 << outBitIndex);
                    }
                    pom1++;
                }
            }
            roundKeys[roundKey] = pc2OnKey;
        }
    }
}

/*
Jak działa algorytm DES? (zmiany w działaniu DESX opisane będą w pliku klasy DESX)
1) podajemy tekst do zaszyfrowania (do wprowadzenia) oraz klucz główny (ustalony z góry)
2) tekst jawny poddajemy initial permutation
3) tekst jawny dzielimy na pół (64 bit -> 2 x 32 bit)
4) klucz główny poddajemy permutacji PC1, dzięki czemu usuniemy też bity parzystości (64 bit -> 56 bit)
* klucz po PC1 zapisujemy na 8 bajtach- dlaczego? bo potrzebujemy 8 bajtów ale korzystamy tylko z 7 bit na bajt
5) klucz główny po permutacji dzielimy na pół (56 bit -> 2 x 28 bit)
* klucz 8 bajtowy zapisujemy na dwóch połówkach po 4 bajty (znowu podobna sytuacja, zapisujemy na 4 bajtach, ale korzystamy
  z 3 pełnych bajtów i połówki ostatniego, bo potrzebujemy 28 bit)
6) tworzymy 16 kluczy rundowych: kazda część podklucza przesuwana jest w lewo o określoną ilość razy
* podklucz rundowy jest zapisany na 4 bajtach, ale korzystamy z odpowiedniej ilości bitów tylko jak wyżej
7) łączymy każdą parę przekształconych podkluczy w jeden klucz 56 bit
* klucz rundowy jest zapisany na 8 bajtach, ale korzystamy z odpowiedniej ilości bitów tylko jak wyżej
8) każdy klucz rundowy poddajemy permutacji PC2
* klucz rundowy po PC2 jest zapisany na 8 bajtach, ale korzystamy z odpowiedniej ilości bitów tylko jak wyżej

Linki do dokumentacji:
- wybor zmiennych (na ilu bajtach są zapisane)
https://docs.oracle.com/cd/E19253-01/817-6223/chp-typeopexpr-2/index.html
- maskowanie na zmiennych z przykladem (&127 &1 i dla maski 28 bit opis)
https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
 */