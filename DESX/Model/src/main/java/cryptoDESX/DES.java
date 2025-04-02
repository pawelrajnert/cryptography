package cryptoDESX;

public class DES {
    private byte[] mainKey;
    private byte[] message;
    private SBox sBox;
    private Permutation permutation;


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
    public void setMainKey(byte[] mainKey) {
        mainKey = "13372115".getBytes();
    }

    // krok 1, wiadomosc jest poddana initial permutation
    private void initialPermutation() {
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
                } else {
                    IPmessage[outByteIndex] &= ~(1 << (7 - outBytePossition)); // jesli rozwazany bit to 0 to wrzucamy 0 do IPmessage
                }
            }
        }
        message = IPmessage;
    }
}
