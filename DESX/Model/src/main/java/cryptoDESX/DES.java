package cryptoDESX;

import java.io.UnsupportedEncodingException;

public class DES {
    private byte[] mainKey;
    private byte[] leftKeyPart = new byte[4];
    private byte[] rightKeyPart = new byte[4];
    private byte[][] roundKeys = new byte[16][8];

    public byte[] getMainKey() {
        return mainKey;
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

    // sprawdzamy czy klucz ma 8 bajtow, a jak nie to dopisujemy do niego spacje
    protected String isKeyCorrect(String key) {
        if (key == null || key.length() == 0 || key.length() > 8) {
            return "abcdefgh"; // jesli klucz jest niepoprawny, to ustawiamy narazie taki domyślny
        }
        while (key.length() < 8) {
            key += " ";
        }

        return key;
    }

    // ustawiamy klucz jesli wprowadzony jest poprawny. Od razu wykonujemy na nim wszystkie operacje,
    // tzn wywołujemy kolejne funkcje, które razem dążą do utworzenia 16 kluczy rundowych.
    public void setMainKey(String key) {
        try {
            this.mainKey = isKeyCorrect(key).getBytes("ISO-8859-1");
            doPC1on56bitKey();
            // w tym miejscu zostanie wywołana jeszcze metoda mainKey56bitSplitter() przez doPC1on56bitKey();
            makeRoundKeys();
            doPC2OnRoundKeys();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    // wyświetlanie liczb w postaci dwójkowej
    public static String arrayToDecimal(byte[] array, String version) {
        StringBuilder sb = new StringBuilder();
        for (byte a : array) {
            String binary = String.format(version, Integer.toBinaryString(a & 0xFF)).replace(' ', '0');
            sb.append(binary).append(" ");
        }
        return sb.toString();
    }

    // Krok 1, wiadomość jest poddana initial permutation.
    // Metoda jest przystosowana również do wykonywania finalnej permutacji, czyli ostatniego kroku,
    // ponieważ wtedy wykonujemy to samo na wiadomości, ale korzystamy z tabeli odwrotnej permutacji
    private byte[] permutation(byte[] message, int isFinalPermutation) {
        int[][][] permutations = {Permutation.IP, Permutation.IP1};
        byte[] IPmessage = new byte[message.length];
        for (int i = 0; i < 8; i++) { // bo tablica IP jest 8 x 8
            for (int j = 0; j < 8; j++) {
                int bitIndex = permutations[isFinalPermutation][i][j] - 1; // pobieramy numer bitu
                int byteIndex = bitIndex / 8; // informacja o tym w ktorym bajcie jest nasz obecnie rozwazany bit
                int bitPosition = bitIndex % 8; // numer bitu w wyzej sprawdzanym bajcie

                // odczytujemy wartosc bitu
                boolean currentBit = ((message[byteIndex] >> (7 - bitPosition)) & 1) == 1;

                int outBitIndex = i * 8 + j; // po permutacji tutaj ma znalezc sie rozwazany bit
                int outByteIndex = outBitIndex / 8; // informacja o bajcie
                int outBytePosition = outBitIndex % 8; // informacja o bicie w bajcie

                if (currentBit) { // jesli rozwazany bit to 1 to wrzucamy go do IPmessage
                    IPmessage[outByteIndex] |= (byte) (1 << (7 - outBytePosition));
                } else { // jesli rozwazany bit to 0 to wrzucamy 0 do IPmessage
                    IPmessage[outByteIndex] &= (byte) ~(1 << (7 - outBytePosition));
                }
            }
        }
        return IPmessage;
    }

    // dzielimy wiadomosc po initial permutation na 2 czesci- lewa i prawa (64 bity -> 2x 32bity)
    private byte[][] messageAfterIPSplitter(byte[] message) {
        if (message.length != 8 || message == null) {
            int pom = message.length;
            throw new IllegalArgumentException("Wiadomość nie ma 8 bajtów, tylko ma ich: " + pom);
        }
        byte[] left = new byte[4];
        byte[] right = new byte[4];

        for (int i = 0; i < 4; i++) {
            left[i] = message[i]; // lewa czesc to bajty: 0, 1, 2, 3 (32 bity)
            right[i] = message[i + 4]; // prawa czesc to bajty: 4, 5, 6, 7 (32 bity)
        }
        return new byte[][]{left, right};   // zwracamy to jako tablicę dwuwymiarową, by z zewnątrz mieć dostęp do obu nar az
    }

    // klucz 64 bitowy poddajemy PC1, dzięki czemu usuniemy też bity parzystości i klucz będzie 56 bit
    private void doPC1on56bitKey() {
        if (mainKey.length != 8 || mainKey == null) {
            int pom = mainKey.length;
            throw new IllegalArgumentException("Klucz nie ma 8 bajtów, tylko ma ich: " + pom);
        }

        byte[] pc1OnKey = new byte[8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                int bitIndex = Permutation.PC1[i][j] - 1; // pobieramy numer bitu (-1, bo indeksujemy od 0)
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
        mainKey56bitSplitter(pc1OnKey); // automatycznie dzielimy nasz klucz na części
    }

    // dzielimy 56 bitowy klucz na 2 x 28 bit podklucze
    private void mainKey56bitSplitter(byte[] pc1OnKey) {
        // klucz powinien miec 56 bitow ALE ZAPISUJEMY TO NA 8 BAJTACH I IGNORUJEMY BIT PARZYSTOŚCI, dlatego sprawdzamy czy ma 8 bajtow
        if (pc1OnKey.length != 8 || pc1OnKey == null) {
            int pom = pc1OnKey.length;
            throw new IllegalArgumentException("Klucz nie ma 8 bajtów, tylko ma ich: " + pom);
        }

        byte[] left = new byte[4];
        byte[] right = new byte[4];

        for (int i = 0; i < 28; i++) { // dodajemy pierwsze 28 bit do lewej części klucza
            int byteIndex = i / 7; // rozwazany bajt (patrzymy gdzie trafi bit, 7 bitow w bajcie rozwazamy)
            int bitPosition = 6 - (i % 7); // pozycja bitu w bajcie

            // odczytujemy wartosc bitu
            boolean currentBit = ((pc1OnKey[byteIndex] >> bitPosition) & 1) == 1;

            if (currentBit == true) { // jesli bit to 1 to tak go ustawiamy
                left[byteIndex] |= (1 << bitPosition);
            } else { // jesli bit to 0 to zostawiamy go jako 0
                left[byteIndex] &= ~(1 << bitPosition);
            }
        }
        for (int j = 28; j < 56; j++) { // dodajemy drugie 28 bit do prawej części klucza
            int byteIndex = (j - 28) / 7; // rozwazany bajt w prawej części klucza
            int bitPossition = 6 - ((j - 28) % 7); // pozycja bitu w bajcie

            // odczytujemy wartosc bitu
            boolean currentBit = ((pc1OnKey[j / 7] >> bitPossition) & 1) == 1;

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
    private void makeRoundKeys() {
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

    private void rotateLeftSingleKey(byte[] key, int shiftAmount) {
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

    private void doPC2OnRoundKeys() { // permutacja działa jak PC1, tylko tym razem robimy z 56 bitów 48 (DALEJ ZAPISUJEMY TO NA 8 BAJTACH, ALE KORZYSTAMY TYLKO Z 6 BITOW NA BAJT)
        for (int roundKey = 0; roundKey < 16; roundKey++) {
            if (roundKeys[roundKey] == null || roundKeys[roundKey].length != 8) {
                int pom = roundKeys[roundKey].length;
                throw new IllegalArgumentException("Klucz rundowy na wejściu nie ma 8 bajtów, tylko ma ich: " + pom);
            }

            byte[] pc2OnKey = new byte[8];
            int pom1 = 0; // licznik bitow (do 48 nas interesuje wartosc, bo tyle zwraca PC2), zmienna pomocnicza

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 6; j++) {
                    int bitIndex = Permutation.PC2[i][j] - 1;
                    // najpierw klucz ma 56 bit wiec interesuje nas 7 bitow z bajta
                    int byteIndex = bitIndex / 7;
                    int bitPossition = 6 - (bitIndex % 7);

                    boolean currentBit = (((roundKeys[roundKey])[byteIndex] >> bitPossition) & 1) == 1;

                    // a potem output po PC2 ma 6 bit w bajcie
                    int outByteIndex = pom1 / 6;
                    int outBitIndex = 5 - (pom1 % 6);

                    if (currentBit == true) {
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
    /*
    Prawą część naszej wiadomości rozszerzamy do 48 bitów, korzystając z EbitTable.
    Wynik zwracamy do metody keyOperation
     */
    private byte[] expandMessageWithEbitTable(byte[] right) {    // rozszerzenie wiadomości przy wykorzystaniu EbitTable
        byte[] result = new byte[8];

        int pom = 0;        // tak jak w przypadku PC2 - licznik bitów, do 48

        for (int i = 0; i < 8; i++) {       // bo 8 bajtów
            for (int j = 0; j < 6; j++) {       // bo 6 bitów w bajcie
                int bitIndex = Permutation.EbitTable[i][j] - 1;         // pobranie indeksu bitu z tabeli
                int byteIndex = bitIndex / 8;                       // obliczenie indeksu bajtu na podstawie indeksu bitu
                int bitPosition = 7 - bitIndex % 8;                 // policzenie pozycji bitu na podstawie indeksu
                boolean currentBit = (((right[byteIndex]) >> bitPosition) & 1) == 1;        // operacje bitowe w celu wskazania wartości
                // output tak jak w PC2 - na 6 bitów
                int outByteIndex = pom / 6;
                int outBitIndex = 5 - (pom % 6);

                if (currentBit) {
                    result[outByteIndex] |= (byte) (1 << outBitIndex);
                } else {
                    result[outByteIndex] &= (byte) ~(1 << outBitIndex);
                }
                pom++;
            }
        }
        return result;
    }
    /*
    Operacja z kluczem. Rozszerzoną wiadomość składającą się z 8 bajtów, na których korzystamy z 6 bitów
    XORujemy z kluczem z danej rundy. Otrzymamy w ten sposób 8 bajtów, po 6 bitów każdy.
    Na podstawie tych wyników odczytamy wartości z sBoxów.
     */
    private byte[] keyOperation(int runda, byte[] right) {
        //
        byte[] result = new byte[8];
        byte[] expandedMessage = expandMessageWithEbitTable(right);
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) (expandedMessage[i] ^ roundKeys[runda - 1][i]);
        }
        return result;
    }
    /*
    Operacja z sBoxem. Mamy 8 sBoxów, dla każdego bajta. Nasze bajty mają 6 bitową postać.
    Przekształcamy każdy 6-bitowy bajt do 4-bitowej postaci
    Na przykładzie, jak to działa:
    n-ty bajt wygląda następująco: 101011. Łączymy ze sobą 1 i 6 bit, oraz bity 2,3,4,5. Otrzymujemy 11 i 0101.
    Wyniki przekształcamy na wartość dziesiętną. Zatem otrzymamy 11 -> 3 oraz 0101 -> 5.
    Są to odpowiednio indeksy wiersza i kolumny z n-tego sBoxa.
    Z nTego sBoxa odczytujemy wartość jaka się tam znajduję (załóżmy: 11).
    Przekształcamy na postać dziesiętną, więc 11 -> 1011. Zatem ten n-ty bajt wyniesie 11.
    UWAGA! Należy pamiętać, że indeksowanie wierszy i kolumn zaczynamy od 0, a nie od 1!
     */
    private byte[] sBoxOperation(int runda, byte[] right) {
        byte[] result = new byte[8];
        byte[] message = keyOperation(runda, right);
        int[][][] sBox = {SBox.SBox1, SBox.SBox2, SBox.SBox3, SBox.SBox4,
                SBox.SBox5, SBox.SBox6, SBox.SBox7, SBox.SBox8};
        for (int i = 0; i < 8; i++) {
            int row = ((message[i] >> 5) & 0b1) << 1 | (message[i] & 0b1); // uzyskiwanie indeksu wiersza
            int col = (message[i] >> 1) & 0b1111;   // uzyskiwanie indeksu kolumny
            int sBoxResult = sBox[i][row][col];
            result[i] = (byte) (sBoxResult ^ result[i]);
        }
        return result;
    }
    // Funkcja F, która bezpośrednio i pośrednio wywołuje inne metody.
    // A sama finalnie wykonuje permutację według tabeli P.
    private byte[] fFunction(int runda, byte[] right) {
        byte[] result = new byte[8];
        byte[] message = sBoxOperation(runda, right);
        int pom = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {       // na 4 bity zapiszemy
                int bitIndex = Permutation.P[i][j] - 1;     // pobranie indeksu z tabeli
                int byteIndex = bitIndex / 4;               // obliczenie, który to bajt
                int bitPosition = 3 - bitIndex % 4;         // obliczenie pozycji bity.
                boolean currentBit = (((message[byteIndex]) >> bitPosition) & 1) == 1;  // operacje bitowe
                int outByteIndex = pom / 4; // wynik przekształcamy na bajt 4-bitowy
                int outBitIndex = 3 - (pom % 4);
                if (currentBit) {
                    result[outByteIndex] |= (byte) (1 << outBitIndex);
                } else {
                    result[outByteIndex] &= (byte) ~(1 << outBitIndex);
                }
                pom++;
            }
        }

        return result;
    }
    /*
    Najważniejsza metoda programu. Przed jej użyciem klucz główny musi być ustawiony.
    Oznaczenia w kodzie odwołują się do podpunktu 9) z instrukcji na dole kodu.
    *) - oznaczenie, w jaki sposób deszyfrujemy wiadomość
     */
    public byte[] encryptMessage(byte[] message, boolean isEncrypt) {
        if (mainKey != null && mainKey.length == 8) {
            message = permutation(message, 0);          // a)
            byte[][] messageParts = messageAfterIPSplitter(message);    // początek b)
            byte[] left = messageParts[0];
            byte[] right = messageParts[1];                             // koniec b)

            for (int j = 1; j <= 16; j++) {                             // c), d), e)
                int runda = j;
                if (isEncrypt) {                                        // *)
                    runda = 17 - j;
                }
                byte[] result = fFunction(runda, right);                // c1 - c4
                byte[] leftCopy = new byte[4];                          // d1
                System.arraycopy(left, 0, leftCopy, 0, 4);
                for (int i = 0; i < 4; i++) {
                    left[i] = right[i];                                                                             // d2
                    right[i] = (byte) ((((result[2 * i] & 0x0F) << 4) | (result[2 * i + 1] & 0x0F)) ^ leftCopy[i]); // d3
                }

            }
            for (int i = 0; i < 4; i++) {
                message[i] = right[i];      // f)
                message[i + 4] = left[i];   // f)
            }
            return permutation(message, 1); // g)
        } else {
            throw new IllegalArgumentException("Nie można zaszyfrować - klucz nie został ustawiony!");
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
9) szyfrujemy wiadomość (w przypadku naszego algorytmu metoda encryptMessage) w następujący sposób:
    a) - Dokonujemy wstępnej permutacji wiadomości, wykorzystując metodę permutation (według tabeli IP).
    b) - Dzielimy wiadomość na dwie równe części — lewą i prawą
    c) - Wykonujemy 16 rund szyfrowania wiadomości wedle schematu:
        c1) - rozszerzamy prawą część wiadomości z 32 do 48 bitów
        c2) - na otrzymanym rozszerzeniu dokonujemy operacji XOR z kluczem z danej rundy
        c3) - działanie z sBoxami, zamysł działania przy metodzie sBoxOperation
        c4) - dokonujemy finalnej permutacji w końcowym etapie rundy w metodzie fFunction
    d) - przechodzimy do kolejnej rundy, wykonując następujące operacje:
        d1) - wykonujemy kopię lewej częśći wiadomości (32 bity)
        d2) - przenosimy prawą część wiadomości do lewej części wiadomości
        d3) - ustalamy nową prawą część wiadomości. Jest to po prostu operacja XOR
              pomiędzy skopiowaną lewą częścią wiadomości a wynikiem rundy (c4)
    e) Wracamy do podpunktu c) do momentu wykonania wszystkich rund
    f) Finalny wynik łączymy w całość, zamieniając kolejność - najpierw prawa część, potem lewa
    g) dokonujemy finalnej permutacji według tabeli IP-1, i zwracamy wynik.

Możemy również deszyfrować wiadomości pod warunkiem, że znamy klucz.
W takim wypadku postępujemy praktycznie tak samo jak w podpunkcie 9), różnica polega na tym,
że klucze rundowe przekazujemy w odwrotnej kolejności - zaczynamy od ostatniego w pierwszej rundzie.


Linki do dokumentacji:
- wybor zmiennych (na ilu bajtach są zapisane)
https://docs.oracle.com/cd/E19253-01/817-6223/chp-typeopexpr-2/index.html
- maskowanie na zmiennych z przykladem (&127 &1 i dla maski 28 bit opis)
https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
- dokumentacja NIST
https://csrc.nist.gov/files/pubs/fips/46/final/docs/nbs.fips.46.pdf
 */