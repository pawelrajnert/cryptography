package cryptoDESX;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaverDao<T> implements Dao<T> {
    public SaverDao() {
    }

    @Override
    public T read(String name) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(name))) {
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void write(String name, T obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(name))) {
            oos.writeObject(obj);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {

    }
}
