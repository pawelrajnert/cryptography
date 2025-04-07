package cryptoDESX;

import java.io.UnsupportedEncodingException;

public class DESX extends DES {
    private final DES des;
    private byte[] initialKey;
    private byte[] finalKey;

    public DESX(DES des) {
        this.des = des;
    }


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
}


