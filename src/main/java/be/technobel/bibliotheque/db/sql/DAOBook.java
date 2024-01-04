package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.model.Book;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOBook implements DAO<Book> {
    private static DAOBook instance;
    private final Connection connection;

    public static DAOBook get() throws SQLException {
        if(instance == null) instance = new DAOBook();
        return instance;
    }

    private DAOBook() throws SQLException {
        connection = SQL_CONNECTION.get();
        try(var stmtCreate = connection.createStatement()) {
            stmtCreate.executeUpdate("""
            create table if not exists book (
                book_id integer primary key,
                book_isbn char(13) unique,
                book_title varchar(50) not null,
                book_author varchar(50) not null,
                book_publication_date char(10)
            );""");
        }
    }

    @Override
    public void add(Book book) throws SQLException {
        try(var stmtInsert = connection.prepareStatement("""
        insert into book (
            book_isbn, book_title, book_author, book_publication_date
        ) values (?,?,?,?);
        """)) {
            stmtInsert.setString(1, book.getISBN());
            stmtInsert.setString(2, book.getTitle());
            stmtInsert.setString(3, book.getAuthor());
            stmtInsert.setString(4, book.getPublicationDate().toString());
            stmtInsert.executeUpdate();
        }
    }

    public void add(Book... books) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        try {
            var stmtInsert = connection.prepareStatement("""
            insert into book (
                book_isbn, book_title, book_author, book_publication_date
            ) values (?,?,?,?);
            """);
            for(var book : books) {
                stmtInsert.setString(1, book.getISBN());
                stmtInsert.setString(2, book.getTitle());
                stmtInsert.setString(3, book.getAuthor());
                stmtInsert.setString(4, book.getPublicationDate().toString());
                stmtInsert.addBatch();
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
    public void update(Book book) throws SQLException {
        try(var stmtUpdate = connection.prepareStatement("""
        update book set
            book_isbn = ?, book_title = ?, book_author = ?, book_publication_date = ?
        where book_id = ?;
        """)) {
            stmtUpdate.setString(1, book.getISBN());
            stmtUpdate.setString(2, book.getTitle());
            stmtUpdate.setString(3, book.getAuthor());
            stmtUpdate.setString(4, book.getPublicationDate().toString());
            stmtUpdate.setInt(5, (int)book.getId());
            stmtUpdate.executeUpdate();
        }
    }

    @Override
    public void delete(Book book) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book where book_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)book.getId());
            stmtDelete.executeUpdate();
        }
    }

    public Optional<Book> get(long id) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select * from book b where book_id = ?;
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

    public Optional<Book> getByISBN(String isbn) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select * from book b where book_isbn = ?;
        """)) {
            stmtSelect.setString(1, isbn);
            try(var resultSet = stmtSelect.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(deserialize(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select * from book b
        order by b.book_title, b.book_author;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<Book>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<Book> find(int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book b
        order by b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setInt(1, limit);
            stmtSelect.setInt(2, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<Book>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<Book> findByTitle(String title, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book b
        where book_title like ? escape '!'
        order by b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(title));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<Book>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<Book> findByAuthor(String author, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book b
        where book_author like ? escape '!'
        order by b.book_author, b.book_title
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(author));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<Book>();
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
    public Book deserialize(ResultSet resultSet) throws SQLException {
        return new Book(
            resultSet.getInt("book_id"),
            resultSet.getString("book_isbn"),
            resultSet.getString("book_title"),
            resultSet.getString("book_author"),
            LocalDate.parse(resultSet.getString("book_publication_date"))
        );
    }

    @Override
    public boolean isFull() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select max(book_id) max_id from book;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("max_id") == Integer.MAX_VALUE;
            }
        }
    }

    public boolean exists(Book book) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select count(book_id) count from book
        where book_id = ? or book_isbn = ?
        limit 1;
        """)) {
            stmtSelect.setInt(1, (int)book.getId());
            stmtSelect.setString(2, book.getISBN());
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        }
    }
}