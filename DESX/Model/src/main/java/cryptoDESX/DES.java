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

    // ustawiamy glowny klucz domyslny algorytmu w naszym przypadku jest to losowa liczba 13372115
    // jednak liczba ma okreslona liczbe znakow (8 znakow = 64 bity)
    public void setMainKey() {
        mainKey = "13372115".getBytes();
    }

    public byte[] getLeftMesPart() {
        return leftMesPart;
    }

    public byte[] getRightMesPart() {
        return rightMesPart;
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
            throw new IllegalArgumentException("Wiadomość na wejściu nie ma 64 bitów, tylko ma ich: " + pom);
        }

        for (int i = 0; i < 4; i++) {
            leftMesPart[i] = message[i]; // lewa czesc to bajty: 0, 1, 2, 3 (32 bity)
            rightMesPart[i] = message[i + 4]; // prawa czesc to bajty: 4, 5, 6, 7 (32 bity)
        }
    }

    // usuwamy bity parzystości z klucza głównego
    public void make56MainKey() {
        if (mainKey.length != 8 || mainKey == null) {
            int pom = mainKey.length;
            throw new IllegalArgumentException("Klucz na wejściu nie ma 64 bitów, tylko ma ich: " + pom);
        }

        byte[] MainKey56 = new byte[7];
        int pom = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 7; j >= 1; j--) { // bierzemy pod uwage wszystkie bity oprocz bitu parzystosci
                boolean currentBit = (mainKey[i] >> j) == 1;

                int outByteIndex = pom / 8; // bajt docelowy
                int outBitIndex = 7 - (pom % 8); // pozycja bitu w bajcie
                if (currentBit == true) { // jesli bit to 1 to wrzucamy go do mainkey56bit
                    MainKey56[outByteIndex] |= (1 << outBitIndex);
                } else { // jesli bit to 0 to wrzucamy go do mainkey56bit
                    MainKey56[outByteIndex] &= ~(1 << outBitIndex);
                }
                pom++;
            }
        }
        mainKey = MainKey56;
    }

    // dzielimy 56 bitowy klucz na 2 x 28 bit podklucze
    public void mainKey56bitSplitter() {
        if (mainKey.length != 7 || mainKey == null) {
            int pom = mainKey.length;
            throw new IllegalArgumentException("Klucz nie jest kluczem bez bitów parzystości (56 bit), tylko ma ich: " + pom);
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

            // łączymy podklucze (2 x 28 bit) w jeden 56 bitowy (przechowywany w roundKeys)
            System.arraycopy(leftKeyPart, 0, roundKeys[i], 0, 4);
            System.arraycopy(rightKeyPart, 0, roundKeys[i], 4, 4);
        }

    }

    public void rotateLeftSingleKey(byte[] key, int shiftAmount) {
        // wykonujemy rotację o jeden bit, shiftAmount razy (korzystamy z wartosci z tablicy).
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

}

/*
Jak działa algorytm DES? (zmiany w działaniu DESX opisane będą w pliku klasy DESX)
1) podajemy tekst do zaszyfrowania (do wprowadzenia) oraz klucz główny (ustalony z góry)
2) tekst jawny poddajemy initial permutation
3) tekst jawny dzielimy na pół (64 bit -> 2 x 32 bit)
4) z klucza głównego usuwamy bity parzystości (64 bit -> 56 bit)
5) klucz główny bez bitów parzystości dzielimy na pół (56 bit -> 2 x 28 bit)
6) tworzymy 16 kluczy rundowych: kazda część podklucza przesuwana jest w lewo o określoną ilość razy
7) łączymy każdą parę przekształconych podkluczy w jeden klucz 56 bit
 */