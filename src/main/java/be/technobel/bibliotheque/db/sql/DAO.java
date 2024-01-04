package be.technobel.bibliotheque.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DAO<T> extends AutoCloseable {
    SQLConnection SQL_CONNECTION = SQLConnection.SQLITE_MEMORY;
    void add(T object) throws SQLException;
    void update(T object) throws SQLException;
    void delete(T object) throws SQLException;
    List<T> findAll() throws SQLException;
    T deserialize(ResultSet resultSet) throws SQLException;
    boolean isFull() throws SQLException;
    boolean exists(T object) throws SQLException;
}
