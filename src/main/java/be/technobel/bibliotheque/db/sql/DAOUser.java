package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.model.auth.UserAccount;
import be.technobel.bibliotheque.model.auth.UserRole;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOUser implements DAO<UserAccount> {
    private static DAOUser instance;
    private final Connection connection;

    public static DAOUser get() throws SQLException {
        if(instance == null) instance = new DAOUser();
        return instance;
    }

    private DAOUser() throws SQLException {
        connection = SQL_CONNECTION.get();
        try(var stmtCreate = connection.createStatement()) {
            stmtCreate.executeUpdate("""
            create table if not exists account (
                account_id integer primary key,
                account_login varchar(50) unique,
                account_secret_hash char(128) not null,
                account_secret_salt char(16) not null,
                account_role tinyint default 1,
                account_first_name varchar(50),
                account_last_name varchar(50),
                account_birth_date char(10),
                account_address_id integer references address
            );""");
        }
    }

    @Override
    public void add(UserAccount user) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        try {
            DAOAddress.get().add(user.getAddress());
            try(var rsId = connection.createStatement().executeQuery("SELECT last_insert_rowid()")) {
                if(!rsId.next()) throw new SQLException();
                long addressId = rsId.getLong(1);
                try(var stmtInsert = connection.prepareStatement("""
                insert into account (
                    account_login, account_secret_hash, account_secret_salt, account_role,
                    account_first_name, account_last_name, account_birth_date, account_address_id
                ) values (?,?,?,?,?,?,?,?);
                """)) {
                    stmtInsert.setString(1, user.getLogin());
                    stmtInsert.setBytes(2, user.getSecretHash());
                    stmtInsert.setBytes(3, user.getSecretSalt());
                    stmtInsert.setInt(4, user.getRole().getValue());
                    stmtInsert.setString(5, user.getFirstName());
                    stmtInsert.setString(6, user.getLastName());
                    stmtInsert.setString(7, user.getBirthDate().toString());
                    stmtInsert.setInt(8, (int)addressId);
                    stmtInsert.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            connection.rollback(savepoint);
            throw e;
        }
    }

    public void add(UserAccount... users) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        try {
            var stmtInsert = connection.prepareStatement("""
            insert into account (
                account_login, account_secret_hash, account_secret_salt, account_role,
                account_first_name, account_last_name, account_birth_date, account_address_id
            ) values (?,?,?,?,?,?,?,?);
            """);
            for(var user : users) {
                DAOAddress.get().add(user.getAddress());
                try(var rsId = connection.createStatement().executeQuery("SELECT last_insert_rowid()")) {
                    if(!rsId.next()) throw new SQLException();
                    long addressId = rsId.getLong(1);
                    stmtInsert.setString(1, user.getLogin());
                    stmtInsert.setBytes(2, user.getSecretHash());
                    stmtInsert.setBytes(3, user.getSecretSalt());
                    stmtInsert.setInt(4, user.getRole().getValue());
                    stmtInsert.setString(5, user.getFirstName());
                    stmtInsert.setString(6, user.getLastName());
                    stmtInsert.setString(7, user.getBirthDate().toString());
                    stmtInsert.setInt(8, (int)addressId);
                    stmtInsert.addBatch();
                }
            }
            stmtInsert.executeBatch();
            stmtInsert.close();
        }
        catch (SQLException e) {
            connection.rollback(savepoint);
            throw e;
        }
    }

    @Override
    public void update(UserAccount user) throws SQLException {
        try(var stmtUpdate = connection.prepareStatement("""
        update account set
            account_login = ?, account_secret_hash = ?, account_secret_salt = ?, account_role = ?,
            account_first_name = ?, account_last_name = ?, account_birth_date = ?, account_address_id = ?
        where account_id = ?;
        """)) {
            stmtUpdate.setString(1, user.getLogin());
            stmtUpdate.setBytes(2, user.getSecretHash());
            stmtUpdate.setBytes(3, user.getSecretSalt());
            stmtUpdate.setInt(4, user.getRole().getValue());
            stmtUpdate.setString(5, user.getFirstName());
            stmtUpdate.setString(6, user.getLastName());
            stmtUpdate.setString(7, user.getBirthDate().toString());
            stmtUpdate.setInt(8, (int)user.getAddress().getId());
            stmtUpdate.setInt(9, (int)user.getId());
            stmtUpdate.executeUpdate();
        }
    }

    @Override
    public void delete(UserAccount user) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from account where account_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)user.getId());
            stmtDelete.executeUpdate();
        }
    }

    public Optional<UserAccount> get(long id) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from account ac
        inner join address ad on ac.account_address_id = ad.address_id
        where ac.account_id = ?;
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

    public Optional<UserAccount> getByLogin(String login) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from account ac
        inner join address ad on ac.account_address_id = ad.address_id
        where ac.account_login = ?;
        """)) {
            stmtSelect.setString(1, login);
            try(var resultSet = stmtSelect.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(deserialize(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserAccount> findAll() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from account ac
        inner join address ad on ac.account_address_id = ad.address_id
        order by ac.account_login;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<UserAccount>();
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

    @Deprecated
    public static String toHex(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            String hex = String.format("%02X", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Deprecated
    public static byte[] fromHex(String hexString) {
        int length = hexString.length();
        if(length % 2 != 0) throw new IllegalArgumentException();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            String hexByte = hexString.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(hexByte, 16);
        }
        return byteArray;
    }

    @Override
    public UserAccount deserialize(ResultSet resultSet) throws SQLException {
        return new UserAccount(
            resultSet.getInt("account_id"),
            resultSet.getString("account_login"),
            resultSet.getBytes("account_secret_hash"),
            resultSet.getBytes("account_secret_salt"),
            UserRole.fromValue(resultSet.getInt("account_role")),
            resultSet.getString("account_last_name"),
            resultSet.getString("account_first_name"),
            LocalDate.parse(resultSet.getString("account_birth_date")),
            DAOAddress.get().deserialize(resultSet)
        );
    }

    @Override
    public boolean isFull() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select max(account_id) max_id from account;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("max_id") == Integer.MAX_VALUE;
            }
        }
    }

    public boolean exists(UserAccount user) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select count(account_id) count from account
        where account_id = ? or account_login = ?
        limit 1;
        """)) {
            stmtSelect.setInt(1, (int)user.getId());
            stmtSelect.setString(2, user.getLogin());
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        }
    }
}
