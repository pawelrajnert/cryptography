package cryptoDESX;

import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryDao<T> implements Dao<T> {
    public BinaryDao() {
    }

    @Override
    public T read(String filePath) {
        try {
            return (T) Files.readAllBytes(Path.of(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void write(String name, T obj) {

    }

    @Override
    public void close() throws Exception {

    }
}
