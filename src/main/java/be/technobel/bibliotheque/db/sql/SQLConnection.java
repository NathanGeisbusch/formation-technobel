package be.technobel.bibliotheque.db.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum SQLConnection {
    SQLITE_MEMORY("jdbc:sqlite::memory:"),
    SQLITE_PERSISTENT("jdbc:sqlite:test.db");

    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private Connection connection = null;

    SQLConnection(String URL) {
        this.URL = URL;
        this.USERNAME = null;
        this.PASSWORD = null;
    }

    SQLConnection(String URL, String USERNAME, String PASSWORD) {
        this.URL = URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    public Connection create() throws SQLException {
        return USERNAME != null && PASSWORD != null ?
            DriverManager.getConnection(URL, USERNAME, PASSWORD) :
            DriverManager.getConnection(URL);
    }

    public Connection get() throws SQLException {
        if(connection == null) {
            if(USERNAME != null && PASSWORD != null)
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            else connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public void clear() throws SQLException {
        if(connection != null) connection.close();
        connection = null;
    }
}
