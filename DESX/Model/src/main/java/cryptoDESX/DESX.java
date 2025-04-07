package cryptoDESX;

import java.io.UnsupportedEncodingException;

public class DESX extends DES {
    private byte[] initialKey;
    private byte[] finalKey;
    
    public void setInitialKey(String initialKey) {
        try {
            this.initialKey = isKeyCorrect(initialKey).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setFinalKey(String finalKey) {
        try {
            this.initialKey = isKeyCorrect(finalKey).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getInitialKey() {
        return initialKey;
    }

    public byte[] getFinalKey() {
        return finalKey;
    }

    public byte[] firstEncrypt(byte[] message) {
        for (int i = 0; i < message.length; i++) {
            message[i] = (byte) (initialKey[i] ^ message[i]);
        }
        return message;
    }
    public byte[] finalEncrypt(byte[] message) {
        for (int i = 0; i < message.length; i++) {
            message[i] = (byte) (finalKey[i] ^ message[i]);
        }
        return message;
    }
}


