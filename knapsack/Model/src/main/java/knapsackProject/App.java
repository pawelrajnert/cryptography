package knapsackProject;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Knapsack knapsack = new Knapsack();
        knapsack.generateKeys();
        String input = "inputText";
        byte[] inputBytes = input.getBytes();
        List<Integer> cipherText = knapsack.encrypt(inputBytes);
        String base64CipherText = DataConverter.showCipherText(cipherText);
        List<Integer> cipherText2 = DataConverter.decodeCipherText(base64CipherText);
        byte[] decodedText = knapsack.decrypt(cipherText2);
        String result = new String(decodedText);
        System.out.println("Wprowadzony tekst: " + input);
        System.out.println("Zaszyfrowany tekst: " + cipherText);
        System.out.println("Do wstawienia w pole: " + base64CipherText);
        System.out.println("Odkodowana lista intów: " + cipherText2);
        System.out.println("Odszyfrowany tekst: " + result);
    }
}
