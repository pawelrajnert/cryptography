package knapsackProject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DataConverter {
    public static String showCipherText(List<Integer> cipherText) {
        ByteBuffer buffer = ByteBuffer.allocate(cipherText.size() * 2);
        for (Integer value : cipherText) {
            buffer.putShort((short) (int) value);
        }
        return Base64.getEncoder().encodeToString(buffer.array());
    }

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
