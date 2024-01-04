package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.model.auth.Address;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOAddress implements DAO<Address> {
    private static DAOAddress instance;
    private final Connection connection;

    public static DAOAddress get() throws SQLException {
        if(instance == null) instance = new DAOAddress();
        return instance;
    }

    private DAOAddress() throws SQLException {
        connection = SQL_CONNECTION.get();
        try(var stmtCreate = connection.createStatement()) {
            stmtCreate.executeUpdate("""
            create table if not exists address (
                address_id integer primary key,
                address_country varchar(50) not null,
                address_city varchar(50) not null,
                address_zip_code varchar(50) not null,
                address_street_name varchar(50),
                address_street_number varchar(50)
            );""");
        }
    }

    @Override
    public void add(Address address) throws SQLException {
        try(var stmtInsert = connection.prepareStatement("""
        insert into address (
            address_country, address_city, address_zip_code,
            address_street_name, address_street_number
        ) values (?,?,?,?,?);
        """)) {
            stmtInsert.setString(1, address.getCountry());
            stmtInsert.setString(2, address.getCity());
            stmtInsert.setString(3, address.getZipCode());
            stmtInsert.setString(4, address.getStreetName());
            stmtInsert.setString(5, address.getStreetNumber());
            stmtInsert.executeUpdate();
        }
    }

    @Override
    public void update(Address address) throws SQLException {
        try(var stmtUpdate = connection.prepareStatement("""
        update address set
            address_country = ?, address_city = ?, address_zip_code = ?,
            address_street_name = ?, address_street_number = ?
        where address_id = ?;
        """)) {
            stmtUpdate.setString(1, address.getCountry());
            stmtUpdate.setString(2, address.getCity());
            stmtUpdate.setString(3, address.getZipCode());
            stmtUpdate.setString(4, address.getStreetName());
            stmtUpdate.setString(5, address.getStreetNumber());
            stmtUpdate.setInt(6, (int)address.getId());
            stmtUpdate.executeUpdate();
        }
    }

    @Override
    public void delete(Address address) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from address where address_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)address.getId());
            stmtDelete.executeUpdate();
        }
    }

    public Optional<Address> get(long id) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select * from address where address_id = ?;
        """)) {
            stmtSelect.setInt(1, (int)id);
            try(var resultSet = stmtSelect.executeQuery()) {
                if(resultSet.next()) return Optional.of(deserialize(resultSet));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Address> findAll() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select * from address
        order by
            address_country, address_zip_code, address_city,
            address_street_name, address_street_number;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<Address>();
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
    public Address deserialize(ResultSet resultSet) throws SQLException {
        return new Address(
            resultSet.getInt("address_id"),
            resultSet.getString("address_country"),
            resultSet.getString("address_city"),
            resultSet.getString("address_zip_code"),
            resultSet.getString("address_street_name"),
            resultSet.getString("address_street_number")
        );
    }

    @Override
    public boolean isFull() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select max(address_id) max_id from address;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("max_id") == Integer.MAX_VALUE;
            }
        }
    }

    @Override
    public boolean exists(Address address) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select count(address_id) count from address
        where address_id = ?
        limit 1;
        """)) {
            stmtSelect.setInt(1, (int)address.getId());
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        }
    }
}
