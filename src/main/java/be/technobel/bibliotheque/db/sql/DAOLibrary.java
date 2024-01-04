package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.model.auth.Library;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOLibrary implements DAO<Library> {
    private static DAOLibrary instance;
    private final Connection connection;

    public static DAOLibrary get() throws SQLException {
        if(instance == null) instance = new DAOLibrary();
        return instance;
    }

    private DAOLibrary() throws SQLException {
        connection = SQL_CONNECTION.get();
        try(var stmtCreate = connection.createStatement()) {
            stmtCreate.executeUpdate("""
            create table if not exists library (
                library_id integer primary key,
                library_name varchar(50),
                library_address_id integer references address
            );""");
        }
    }

    @Override
    public void add(Library library) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        try {
            DAOAddress.get().add(library.getAddress());
            try(var rsId = connection.createStatement().executeQuery("SELECT last_insert_rowid()")) {
                if(!rsId.next()) throw new SQLException();
                long addressId = rsId.getLong(1);
                try(var stmtInsert = connection.prepareStatement("""
                insert into library (
                    library_name, library_address_id
                ) values (?,?);
                """)) {
                    stmtInsert.setString(1, library.getName());
                    stmtInsert.setInt(2, (int)addressId);
                    if(stmtInsert.executeUpdate() == 0) throw new SQLException();
                }
            }
        }
        catch (SQLException e) {
            connection.rollback(savepoint);
            throw e;
        }
    }

    @Override
    public void update(Library library) throws SQLException {
        try(var stmtUpdate = connection.prepareStatement("""
        update library set
            library_name = ?, library_address_id = ?
        where library_id = ?;
        """)) {
            stmtUpdate.setString(1, library.getName());
            stmtUpdate.setInt(2, (int)library.getAddress().getId());
            stmtUpdate.setInt(3, (int)library.getId());
            stmtUpdate.executeUpdate();
        }
    }

    @Override
    public void delete(Library library) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from library where library_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)library.getId());
            stmtDelete.executeUpdate();
        }
    }

    public Optional<Library> get(long id) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from library l
        inner join address ad on l.library_address_id = ad.address_id
        where l.library_id = ?;
        """)) {
            stmtSelect.setInt(1, (int)id);
            try(var resultSet = stmtSelect.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(deserialize(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Library> findAll() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from library l
        inner join address ad on l.library_address_id = ad.address_id
        order by l.library_name;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<Library>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
        instance = null;
    }

    @Override
    public Library deserialize(ResultSet resultSet) throws SQLException {
        return new Library(
            resultSet.getInt("library_id"),
            resultSet.getString("library_name"),
            DAOAddress.get().deserialize(resultSet)
        );
    }

    @Override
    public boolean isFull() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select max(library_id) max_id from library;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("max_id") == Integer.MAX_VALUE;
            }
        }
    }

    @Override
    public boolean exists(Library library) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select count(library_id) count from library
        where library_id = ?
        limit 1;
        """)) {
            stmtSelect.setInt(1, (int)library.getId());
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        }
    }
}