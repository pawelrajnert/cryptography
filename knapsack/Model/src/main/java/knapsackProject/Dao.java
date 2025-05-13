package knapsackProject;

public interface Dao<T> extends AutoCloseable {
    T read(String name);

    void write(String name, T obj);
}
