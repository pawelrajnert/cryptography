package knapsackProject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DataConverter {
    /*
    Metoda do konwersji listy Integerów na ciąg znaków zapisany w Base64.
    Służy temu, by pokazać zaszyfrowany tekst w polu.
    Każdego Integera zapisujemy na 2 bajtach
     */
    public static String showCipherText(List<Integer> cipherText) {
        ByteBuffer buffer = ByteBuffer.allocate(cipherText.size() * 2);
        for (Integer value : cipherText) {
            buffer.putShort((short) (int) value);   // na początku przekształcamy na int, potem na short - 2 bajtowy
        }
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    /*
    Metoda dokonująca odwrotnej konwersji-ze Stringa zapisanego w base64 na listę Integerów.
    Służy temu, by zaszyfrowany tekst odpowiednio przekazać do metody deszyfrującej.
    Każdy Integer jest zapisany na 2 bajtach, więc pobieramy z bufora 2 bajty i konwertujemy to odpowiednio.
    Wynik dodajemy do listy, którą na koniec zwracamy.
     */
    public static List<Integer> decodeCipherText(String base64String) {
        byte[] bytes = Base64.getDecoder().decode(base64String);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        List<Integer> result = new ArrayList<>();
        while (buffer.remaining() >= 2) {
            result.add((int) buffer.getShort());
        }
        return result;
    }
}
