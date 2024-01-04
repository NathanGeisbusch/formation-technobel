package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.Library;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOBookStock implements DAO<BookStock> {
    private static DAOBookStock instance;
    private final Connection connection;

    public static DAOBookStock get() throws SQLException {
        if(instance == null) instance = new DAOBookStock();
        return instance;
    }

    private DAOBookStock() throws SQLException {
        connection = SQL_CONNECTION.get();
        try(var stmtCreate = connection.createStatement()) {
            stmtCreate.executeUpdate("""
            create table if not exists book_stock (
                book_stock_library_id integer references account,
                book_stock_book_id integer references book,
                book_stock_quantity bigint default 0,
                primary key (book_stock_library_id, book_stock_book_id)
            );""");
        }
    }

    @Override
    public void add(BookStock bookStock) throws SQLException {
        try(var stmtInsert = connection.prepareStatement("""
        insert into book_stock (
            book_stock_library_id, book_stock_book_id, book_stock_quantity
        ) values (?,?,?);
        """)) {
            stmtInsert.setInt(1, (int)bookStock.getLibrary().getId());
            stmtInsert.setInt(2, (int)bookStock.getBook().getId());
            stmtInsert.setInt(3, bookStock.getQuantity());
            stmtInsert.executeUpdate();
        }
    }

    public void add(BookStock... bookStocks) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        try {
            var stmtInsert = connection.prepareStatement("""
            insert into book_stock (
                book_stock_library_id, book_stock_book_id, book_stock_quantity
            ) values (?,?,?);
            """);
            for(var bookStock : bookStocks) {
                stmtInsert.setInt(1, (int)bookStock.getLibrary().getId());
                stmtInsert.setInt(2, (int)bookStock.getBook().getId());
                stmtInsert.setInt(3, bookStock.getQuantity());
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
    public void update(BookStock bookStock) throws SQLException {
        try(var stmtUpdate = connection.prepareStatement("""
        update book_stock set book_stock_quantity = ?
        where book_stock_library_id = ? and book_stock_book_id = ?;
        """)) {
            stmtUpdate.setInt(1, bookStock.getQuantity());
            stmtUpdate.setInt(2, (int)bookStock.getLibrary().getId());
            stmtUpdate.setInt(3, (int)bookStock.getBook().getId());
            stmtUpdate.executeUpdate();
        }
    }

    @Override
    public void delete(BookStock bookStock) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book_stock
        where book_stock_library_id = ? and book_stock_book_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)bookStock.getLibrary().getId());
            stmtDelete.setInt(2, (int)bookStock.getBook().getId());
            stmtDelete.executeUpdate();
        }
    }

    public void deleteBy(Library library) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book_stock
        where book_stock_library_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)library.getId());
            stmtDelete.executeUpdate();
        }
    }

    public void deleteBy(Book book) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book_stock
        where book_stock_book_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)book.getId());
            stmtDelete.executeUpdate();
        }
    }

    public Optional<BookStock> get(long libraryId, long bookId) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from book_stock bs
        inner join library l on bs.book_stock_library_id = l.library_id
        inner join address ad on l.library_address_id = ad.address_id
        inner join book b on bs.book_stock_book_id = b.book_id
        where bs.book_stock_library_id = ? and bs.book_stock_book_id = ?;
        """)) {
            stmtSelect.setInt(1, (int)libraryId);
            stmtSelect.setInt(2, (int)bookId);
            try(var resultSet = stmtSelect.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(deserialize(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<BookStock> findAll() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from book_stock bs
        inner join library l on bs.book_stock_library_id = l.library_id
        inner join address ad on l.library_address_id = ad.address_id
        inner join book b on bs.book_stock_book_id = b.book_id
        order by l.library_name, b.book_title, b.book_author;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookStock>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookStock> findBy(Library library, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_stock bs
        inner join library l on bs.book_stock_library_id = l.library_id
        inner join address ad on l.library_address_id = ad.address_id
        inner join book b on bs.book_stock_book_id = b.book_id
        where l.library_id = ?
        order by b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setInt(1, (int)library.getId());
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookStock>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookStock> findBy(Book book, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_stock bs
        inner join library l on bs.book_stock_library_id = l.library_id
        inner join address ad on l.library_address_id = ad.address_id
        inner join book b on bs.book_stock_book_id = b.book_id
        where b.book_id = ?
        order by l.library_name
        limit ? offset ?;
        """)) {
            stmtSelect.setInt(1, (int)book.getId());
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookStock>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookStock> findByBookTitle(String title, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_stock bs
        inner join library l on bs.book_stock_library_id = l.library_id
        inner join address ad on l.library_address_id = ad.address_id
        inner join book b on bs.book_stock_book_id = b.book_id
        where b.book_title like ? escape '!'
        order by b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(title));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookStock>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookStock> findByBookAuthor(String author, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_stock bs
        inner join library l on bs.book_stock_library_id = l.library_id
        inner join address ad on l.library_address_id = ad.address_id
        inner join book b on bs.book_stock_book_id = b.book_id
        where b.book_author like ? escape '!'
        order by b.book_author, b.book_title
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(author));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookStock>();
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
    public BookStock deserialize(ResultSet resultSet) throws SQLException {
        return new BookStock(
            DAOLibrary.get().deserialize(resultSet),
            DAOBook.get().deserialize(resultSet),
            resultSet.getInt("book_stock_quantity")
        );
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean exists(BookStock bookStock) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select count(bs.book_stock_library_id) count from book_stock bs
        where bs.book_stock_library_id = ? and bs.book_stock_book_id = ?
        limit 1;
        """)) {
            stmtSelect.setInt(1, (int)bookStock.getLibrary().getId());
            stmtSelect.setInt(2, (int)bookStock.getBook().getId());
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        }
    }
}