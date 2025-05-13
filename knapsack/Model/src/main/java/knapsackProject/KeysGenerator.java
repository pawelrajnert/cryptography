package knapsackProject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KeysGenerator {

    /*
    Generujemy klucz prywatny, który służy do deszyfrowania wiadomości.
    Jest to ciąg liczb, w naszym przypadku klucz składa się z 8 liczb.
    Przy generowaniu klucza ważne jest, by każda kolejna liczba była większa niż suma pozostałych.
    Na podstawie wygenerowanego klucza prywatnego generujemy n, m i klucz publiczny.
     */
    public static List<Integer> generatePrivateKey() {
        List<Integer> privateKey = new ArrayList<>();
        Random rand = new Random();
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            int randomInteger = rand.nextInt(8) + sum + 1;
            privateKey.add(randomInteger);
            sum += randomInteger;
        }
        return privateKey;
    }

    /*
    Wyliczamy klucz publiczny, który służy do szyfrowania wiadomości.
    Każdą wartość z klucza prywatnego przeliczamy według wzoru:
    public = (private * n) % m,
    gdzie: public - wynik dla klucza publicznego, private - wartość z klucza prywatnego.
     */
    public static List<Integer> generatePublicKey(int m, int n, List<Integer> privateKey) {
        List<Integer> publicKey = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int value = (privateKey.get(i) * n) % m;
            publicKey.add(value);
        }
        return publicKey;
    }

    /*
    Generujemy m.
    Jedyne ograniczenie jest takie, że musi być większe niż suma wszystkich wartości z klucza prywatnego.
    W naszej implementacji liczba o od 1 do 200 większa niż ta suma
     */
    public static int generateM(List<Integer> privateKey) {
        int m = 0;
        for (Integer integer : privateKey) {
            m += integer;
        }
        Random rand = new Random();
        m += rand.nextInt(200) + 1;
        return m;
    }

    /*
    Generujemy n.
    Musi być względnie pierwsze z wygenerowanym wcześniej m, tzn. ich NWD musi wynosić 1.
     */
    public static int generateN(int m) {
        int n;
        Random rand = new Random();
        do {
            n = rand.nextInt(m - 2) + 2;
        } while (numbersAreNotRelativelyPrime(m, n));
        return n;
    }

    /*
    Wykorzystanie algorytmu Euklidesa do weryfikacji, czy liczby są względnie pierwsze
     */
    public static boolean numbersAreNotRelativelyPrime(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a != 1;
    }

    /*
    Weryfikacja danych dotyczących klucza prywatnego, m i n
     */
    public static void verifyData(List<Integer> privateKey, int m, int n) {
        if (n > m) {
            throw new IllegalArgumentException("n musi być mniejsze od m");
        }
        if (numbersAreNotRelativelyPrime(m, n)) {
            throw new IllegalArgumentException("m i n nie są względnie pierwsze!");
        }
        if (privateKey.size() != 8) {
            throw new IllegalArgumentException("Prywatny klucz musi składać się z 8 Integerów!");
        }
        int sum = 0;
        for (int value : privateKey) {
            if (value <= 0) {
                throw new IllegalArgumentException("Prywatny klucz zawiera zero lub ujemną wartość!");
            }
            if (value <= sum) {
                throw new IllegalArgumentException("Ciąg liczb z klucza prywatnego nie jest ściśle rosnący");
            }
            sum += value;
        }
        if (sum >= m) {
            throw new IllegalArgumentException("Suma ciągu liczb z klucza prywatnego musi być mniejsze od m");
        }
    }
}
