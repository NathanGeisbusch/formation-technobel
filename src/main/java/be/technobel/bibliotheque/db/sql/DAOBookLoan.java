package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.auth.UserAccount;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOBookLoan implements DAO<BookLoan> {
    private static DAOBookLoan instance;
    private final Connection connection;

    public static DAOBookLoan get() throws SQLException {
        if(instance == null) instance = new DAOBookLoan();
        return instance;
    }

    private DAOBookLoan() throws SQLException {
        connection = SQL_CONNECTION.get();
        try(var stmtCreate = connection.createStatement()) {
            stmtCreate.executeUpdate("""
            create table if not exists book_loan (
                book_loan_account_id integer references account,
                book_loan_book_id integer references book,
                book_loan_loan_date char(10) not null,
                book_loan_return_date char(10),
                book_loan_loan_quantity bigint default 1,
                book_loan_return_quantity bigint default 0,
                primary key (book_loan_account_id, book_loan_book_id, book_loan_loan_date)
            );""");
        }
    }

    @Override
    public void add(BookLoan bookLoan) throws SQLException {
        try(var stmtInsert = connection.prepareStatement("""
        insert into book_loan (
            book_loan_account_id, book_loan_book_id, book_loan_loan_date,
            book_loan_return_date, book_loan_loan_quantity, book_loan_return_quantity
        ) values (?,?,?,?,?,?);
        """)) {
            stmtInsert.setInt(1, (int)bookLoan.getUser().getId());
            stmtInsert.setInt(2, (int)bookLoan.getBook().getId());
            stmtInsert.setString(3, bookLoan.getLoanDate().toString());
            if(bookLoan.getReturnDate() == null) stmtInsert.setNull(4, Types.NULL);
            else stmtInsert.setString(4, bookLoan.getReturnDate().toString());
            stmtInsert.setInt(5, bookLoan.getLoanQuantity());
            stmtInsert.setInt(6, bookLoan.getReturnQuantity());
            stmtInsert.executeUpdate();
        }
    }

    public void add(BookLoan... bookLoans) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        try {
            var stmtInsert = connection.prepareStatement("""
            insert into book_loan (
                book_loan_account_id, book_loan_book_id, book_loan_loan_date,
                book_loan_return_date, book_loan_loan_quantity, book_loan_return_quantity
            ) values (?,?,?,?,?,?);
            """);
            for(var bookLoan : bookLoans) {
                stmtInsert.setInt(1, (int)bookLoan.getUser().getId());
                stmtInsert.setInt(2, (int)bookLoan.getBook().getId());
                stmtInsert.setString(3, bookLoan.getLoanDate().toString());
                if(bookLoan.getReturnDate() == null) stmtInsert.setNull(4, Types.NULL);
                else stmtInsert.setString(4, bookLoan.getReturnDate().toString());
                stmtInsert.setInt(5, bookLoan.getLoanQuantity());
                stmtInsert.setInt(6, bookLoan.getReturnQuantity());
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
    public void update(BookLoan bookLoan) throws SQLException {
        try(var stmtUpdate = connection.prepareStatement("""
        update book_loan set
            book_loan_return_date = ?, book_loan_loan_quantity = ?, book_loan_return_quantity = ?
        where book_loan_account_id = ? and book_loan_book_id = ? and book_loan_loan_date = ?;
        """)) {
            if(bookLoan.getReturnDate() == null) stmtUpdate.setNull(1, Types.NULL);
            else stmtUpdate.setString(1, bookLoan.getReturnDate().toString());
            stmtUpdate.setInt(2, bookLoan.getLoanQuantity());
            stmtUpdate.setInt(3, bookLoan.getReturnQuantity());
            stmtUpdate.setInt(4, (int)bookLoan.getUser().getId());
            stmtUpdate.setInt(5, (int)bookLoan.getBook().getId());
            stmtUpdate.setString(6, bookLoan.getLoanDate().toString());
            stmtUpdate.executeUpdate();
        }
    }

    @Override
    public void delete(BookLoan bookLoan) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book_loan
        where book_loan_account_id = ? and book_loan_book_id = ? and book_loan_loan_date = ?;
        """)) {
            stmtDelete.setInt(1, (int)bookLoan.getUser().getId());
            stmtDelete.setInt(2, (int)bookLoan.getBook().getId());
            stmtDelete.setString(3, bookLoan.getLoanDate().toString());
            stmtDelete.executeUpdate();
        }
    }

    public void deleteBy(UserAccount user) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book_loan
        where book_loan_account_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)user.getId());
            stmtDelete.executeUpdate();
        }
    }

    public void deleteBy(Book book) throws SQLException {
        try(var stmtDelete = connection.prepareStatement("""
        delete from book_loan
        where book_loan_book_id = ?;
        """)) {
            stmtDelete.setInt(1, (int)book.getId());
            stmtDelete.executeUpdate();
        }
    }

    public Optional<BookLoan> get(long userId, long bookId, LocalDate loanDate) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where bl.book_loan_account_id = ? and bl.book_loan_book_id = ? and bl.book_loan_loan_date = ?;
        """)) {
            stmtSelect.setInt(1, (int)userId);
            stmtSelect.setInt(2, (int)bookId);
            stmtSelect.setString(3, loanDate.toString());
            try(var resultSet = stmtSelect.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(deserialize(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<BookLoan> findAll() throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select *
        from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        order by bl.book_loan_loan_date, ac.account_login, b.book_title, b.book_author;
        """)) {
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findBy(UserAccount user, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where ac.account_id = ?
        order by bl.book_loan_loan_date, b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setInt(1, (int)user.getId());
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findBy(Book book, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where b.book_id = ?
        order by bl.book_loan_loan_date, ac.account_login
        limit ? offset ?;
        """)) {
            stmtSelect.setInt(1, (int)book.getId());
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findByLogin(String login, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where ac.account_login like ? escape '!'
        order by ac.account_login, bl.book_loan_loan_date, b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(login));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findByBookTitle(String title, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where b.book_title like ? escape '!'
        order by b.book_title, b.book_author, bl.book_loan_loan_date, ac.account_login
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(title));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findByBookAuthor(String author, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where b.book_author like ? escape '!'
        order by b.book_author, b.book_title, bl.book_loan_loan_date, ac.account_login
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, SQLHelper.escapeLikePatternGlobal(author));
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findByDate(LocalDate loanDate, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where bl.book_loan_loan_date = ?
        order by ac.account_login, b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, loanDate.toString());
            stmtSelect.setInt(2, limit);
            stmtSelect.setInt(3, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findByDateRange(LocalDate startDate, LocalDate endDate, int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where bl.book_loan_loan_date between ? and ?
        order by bl.book_loan_loan_date, ac.account_login, b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setString(1, startDate.toString());
            stmtSelect.setString(2, endDate.toString());
            stmtSelect.setInt(3, limit);
            stmtSelect.setInt(4, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
                while(resultSet.next()) result.add(deserialize(resultSet));
                return result;
            }
        }
    }

    public List<BookLoan> findByPending(int skip, int limit) throws SQLException {
        if(limit == 0) limit = -1;
        try(var stmtSelect = connection.prepareStatement("""
        select * from book_loan bl
        inner join account ac on bl.book_loan_account_id = ac.account_id
        inner join address ad on ac.account_address_id = ad.address_id
        inner join book b on bl.book_loan_book_id = b.book_id
        where bl.book_loan_loan_quantity = bl.book_loan_return_quantity
        order by bl.book_loan_loan_date, ac.account_login, b.book_title, b.book_author
        limit ? offset ?;
        """)) {
            stmtSelect.setInt(1, limit);
            stmtSelect.setInt(2, skip);
            try(var resultSet = stmtSelect.executeQuery()) {
                var result = new ArrayList<BookLoan>();
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
    public BookLoan deserialize(ResultSet resultSet) throws SQLException {
        String returnDate = resultSet.getString("book_loan_return_date");
        boolean isNotReturned = resultSet.wasNull();
        return new BookLoan(
            DAOUser.get().deserialize(resultSet),
            DAOBook.get().deserialize(resultSet),
            LocalDate.parse(resultSet.getString("book_loan_loan_date")),
            isNotReturned ? null : LocalDate.parse(returnDate),
            resultSet.getInt("book_loan_loan_quantity"),
            resultSet.getInt("book_loan_return_quantity")
        );
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean exists(BookLoan bookLoan) throws SQLException {
        try(var stmtSelect = connection.prepareStatement("""
        select count(bl.book_loan_account_id) count from book_loan bl
        where bl.book_loan_account_id = ? and bl.book_loan_book_id = ? and bl.book_loan_loan_date = ?
        limit 1;
        """)) {
            stmtSelect.setInt(1, (int)bookLoan.getUser().getId());
            stmtSelect.setInt(2, (int)bookLoan.getBook().getId());
            stmtSelect.setString(3, bookLoan.getLoanDate().toString());
            try(var resultSet = stmtSelect.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") > 0;
            }
        }
    }
}