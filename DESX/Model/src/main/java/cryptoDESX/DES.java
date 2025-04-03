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
    public void MainKey56bitSplitter() {
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
}
