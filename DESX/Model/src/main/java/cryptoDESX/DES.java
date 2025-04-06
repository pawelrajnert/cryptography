package cryptoDESX;

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
        String messageString = new String(message);
        messageString = isMessageCorrect(messageString);
        this.message = messageString.getBytes();
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
        String keyString = new String(key);
        keyString = isKeyCorrect(keyString);
        this.mainKey = keyString.getBytes();
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
        int pom = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                int bitIndex = permutation.PC1[i][j] - 1; // pobieramy numer bitu
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
                pom++;
            }
        }
        mainKey = pc1OnKey;
    }

    // dzielimy 56 bitowy klucz na 2 x 28 bit podklucze
    public void mainKey56bitSplitter() {
        if (mainKey.length != 8 || mainKey == null) {
            int pom = mainKey.length;
            throw new IllegalArgumentException("Klucz nie ma 8 bajtów, tylko ma ich: " + pom);
        }
        for (int i = 0; i < 28; i++) { // dodajemy pierwsze 28 bit do lewej części klucza
            int byteIndex = i / 8; // rozwazany bajt
            int bitPossition = 7 - (i % 8); // pozycja bitu w bajcie

            // odczytujemy wartosc bitu
            boolean currentBit = ((mainKey[byteIndex] >> bitPossition) & 1) == 1;

            if (currentBit == true) { // jesli bit to 1 to tak go ustawiamy
                leftKeyPart[byteIndex] |= (1 << bitPossition);
            } else { // jesli bit to 0 to zostawiamy go jako 0
                leftKeyPart[byteIndex] &= ~(1 << bitPossition);
            }
        }
        for (int j = 28; j < 56; j++) { // dodajemy drugie 28 bit do prawej części klucza
            int byteIndex = (j / 8); // rozwazany bajt
            int bitPossition = 7 - (j % 8); // pozycja bitu w bajcie

            // odczytujemy wartosc bitu
            boolean currentBit = ((mainKey[byteIndex] >> bitPossition) & 1) == 1;

            if (currentBit == true) { // jesli bit to 1 to tak go ustawiamy
                rightKeyPart[byteIndex - 3] |= (1 << bitPossition);
            } else { // jesli bit to 0 to zostawiamy go jako 0
                rightKeyPart[byteIndex - 3] &= ~(1 << bitPossition);
            }
        }
    }

    // tworzymy 16 kluczy dla kazdej z rund
    public void makeRoundKeys() {
        if (leftKeyPart.length != 4 || rightKeyPart.length != 4) {
            int pomL = leftKeyPart.length;
            int pomR = rightKeyPart.length;
            throw new IllegalArgumentException("Podklucze nie mają po 28 bit, tylko mają kolejno, lewy: " + pomL + " prawy: " + pomR);
        }

        final int[] shiftsReader = Permutation.getShifts();

        for (int i = 0; i < 16; i++) {
            rotateLeftSingleKey(leftKeyPart, shiftsReader[i]); // rotujemy o daną liczbę lewą część klucza w zależności od rundy
            rotateLeftSingleKey(rightKeyPart, shiftsReader[i]); // rotujemy o daną liczbę prawą część klucza w zależności od rundy

            // część kodu poniżej ma na celu złączyć dwa 28 bitowe podklucze w jeden klucz zapisany dokładnie na 56 bitach czyli 7 bajtach
            int leftPart = ((leftKeyPart[0]) << 20) | ((leftKeyPart[1]) << 12) | ((leftKeyPart[2]) << 4) | ((leftKeyPart[3]) >> 4);

            int rightPart = ((rightKeyPart[0]) << 20) | ((rightKeyPart[1]) << 12) | ((rightKeyPart[2]) << 4) | ((rightKeyPart[3]) >> 4);

            long fullKey = (((long) leftPart) << 28) | ((rightPart) & 0x0FFFFFFF); // maska 28 bit (0x0FFFFFFF) zeby tylko tyle brac pod uwage

            for (int j = 0; j < 7; j++) { // przepisujemy wartosci do naszej tablicy z kluczami rundowymi, łącznie mamy 7 bajtów
                roundKeys[i][j] = (byte) (fullKey >>> ((6 - j) * 8));
            }
        }
    }

    public void rotateLeftSingleKey(byte[] key, int shiftAmount) {
        // wykonujemy rotację o jeden bit, shiftAmount razy (korzystamy z wartosci z tablicy)
        for (int i = 0; i < shiftAmount; i++) {

            // bit najbardziej po lewo w bajcie
            boolean leftBit = ((key[0] >> 7) & 1) == 1;

            // przesuwamy pierwsze pelne 3 bajty (po 8 bit)
            for (int j = 0; j < 3; j++) {

                // bit najbardziej po lewo z kolejnego bajtu
                boolean nextLeftBit = ((key[j + 1] >> 7) & 1) == 1;

                key[j] = (byte) ((key[j] << 1));
                if (nextLeftBit == true) {
                    key[j] |= 1; // przenosimy najbardziej na prawo
                } else {
                    key[j] &= ~1;
                }
            }

            // rozwazamy ostatni niepelny bajt do przesuwania
            int smallByte = (key[3] >> 4); // bierzemy tylko 4 bity (28 - (3 x 8) = 4 bit), bo to ten niepelny bajt
            smallByte = (smallByte << 1);

            if (leftBit == true) {
                smallByte |= 1; // przenosimy najbardziej na prawo
            } else {
                smallByte &= ~1;
            }
            key[3] = (byte) (smallByte << 4);
        }
    }

    public void doPC2OnRoundKeys() { // permutacja działa jak PC1, tylko tym razem robimy z 56 bitów 48
        for (int roundKey = 0; roundKey < 16; roundKey++) {
            if (roundKeys[roundKey] == null || roundKeys[roundKey].length != 8) {
                int pom = roundKeys[roundKey].length;
                throw new IllegalArgumentException("Klucz rundowy na wejściu nie ma 56 bitów, tylko ma ich: " + pom);
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
5) klucz główny po permutacji dzielimy na pół (56 bit -> 2 x 28 bit)
6) tworzymy 16 kluczy rundowych: kazda część podklucza przesuwana jest w lewo o określoną ilość razy
7) łączymy każdą parę przekształconych podkluczy w jeden klucz 56 bit
8) każdy klucz rundowy poddajemy permutacji PC2
 */