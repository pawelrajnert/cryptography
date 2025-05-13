package knapsackProject;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Knapsack knapsack = new Knapsack();
        byte[] bytes = "Zaszyfrowany Tekst".getBytes();
        List<Integer> cipherText = knapsack.encrypt(bytes);
        byte[] result = knapsack.decryptCipherText(cipherText);
        System.out.println(cipherText);
        /*
        TODO (prawdopodobnie już przy robieniu GUI)
        Przerobienie listy integerów na to by wyglądało to jak tekst zaszyfrowany xd
         */
        System.out.println(Knapsack.arrayToDecimal(bytes, "%8s"));
        System.out.println();
        System.out.println(Knapsack.arrayToDecimal(result, "%8s"));
    }
}
