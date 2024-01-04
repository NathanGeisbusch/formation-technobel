package be.technobel.bibliotheque.db.sql;

import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.Library;
import be.technobel.bibliotheque.model.auth.UserAccount;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SQLiteDB implements Database {
    public SQLiteDB() throws DatabaseException {
        try {
            DAOAddress.get();
            DAOLibrary.get();
            DAOUser.get();
            DAOBook.get();
            DAOBookStock.get();
            DAOBookLoan.get();
        } catch (Exception e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void close() throws DatabaseException {
        try {
            DAOAddress.get().close();
            DAOLibrary.get().close();
            DAOUser.get().close();
            DAOBook.get().close();
            DAOBookStock.get().close();
            DAOBookLoan.get().close();
        } catch (Exception e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(UserAccount user) throws DatabaseException {
        try {
            if(user == null) throw new IllegalArgumentException();
            if(DAOAddress.get().isFull()) throw new AddressNoRemainingIdException();
            if(DAOUser.get().isFull()) throw new UserNoRemainingIdException();
            if(DAOUser.get().exists(user)) throw new UserAlreadyExistsException();
            DAOUser.get().add(user);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(UserAccount... users) throws DatabaseException {
        try {
            if(users == null) throw new IllegalArgumentException();
            DAOUser.get().add(users);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void update(UserAccount user) throws DatabaseException {
        try {
            if(user == null) throw new IllegalArgumentException();
            if(!DAOUser.get().exists(user)) throw new UserNotFoundException();
            DAOUser.get().update(user);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void delete(UserAccount user) throws DatabaseException {
        try {
            if(user == null) throw new IllegalArgumentException();
            if(!DAOUser.get().exists(user)) throw new UserNotFoundException();
            DAOUser.get().delete(user);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public Optional<UserAccount> getUser(long id) throws DatabaseException {
        if(id < 0) throw new IllegalArgumentException();
        try {
            return DAOUser.get().get(id);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public Optional<UserAccount> getUserByLogin(String login) throws DatabaseException {
        if(login == null || login.isBlank()) throw new IllegalArgumentException();
        try {
            return DAOUser.get().getByLogin(login);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(Library library) throws DatabaseException {
        try {
            if(library == null) throw new IllegalArgumentException();
            if(DAOAddress.get().isFull()) throw new AddressNoRemainingIdException();
            if(DAOLibrary.get().isFull()) throw new LibraryNoRemainingIdException();
            if(DAOLibrary.get().exists(library)) throw new LibraryAlreadyExistsException();
            DAOLibrary.get().add(library);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void update(Library library) throws DatabaseException {
        try {
            if(library == null) throw new IllegalArgumentException();
            if(!DAOLibrary.get().exists(library)) throw new LibraryNotFoundException();
            DAOLibrary.get().update(library);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void delete(Library library) throws DatabaseException {
        try {
            if(library == null) throw new IllegalArgumentException();
            if(!DAOLibrary.get().exists(library)) throw new LibraryNotFoundException();
            DAOLibrary.get().update(library);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public Optional<Library> getLibrary(long id) throws DatabaseException {
        if(id < 0) throw new IllegalArgumentException();
        try {
            return DAOLibrary.get().get(id);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(Book book) throws DatabaseException {
        try {
            if(book == null) throw new IllegalArgumentException();
            if(DAOBook.get().isFull()) throw new BookNoRemainingIdException();
            if(DAOBook.get().exists(book)) throw new BookAlreadyExistsException();
            DAOBook.get().add(book);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(Book... books) throws DatabaseException {
        try {
            if(books == null) throw new IllegalArgumentException();
            DAOBook.get().add(books);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void update(Book book) throws DatabaseException {
        try {
            if(book == null) throw new IllegalArgumentException();
            if(!DAOBook.get().exists(book)) throw new BookNotFoundException();
            DAOBook.get().update(book);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void delete(Book book) throws DatabaseException {
        try {
            if(book == null) throw new IllegalArgumentException();
            if(!DAOBook.get().exists(book)) throw new BookNotFoundException();
            DAOBook.get().delete(book);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public Optional<Book> getBook(long id) throws DatabaseException {
        if(id < 0) throw new IllegalArgumentException();
        try {
            return DAOBook.get().get(id);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public Optional<Book> getBookByISBN(String isbn) throws DatabaseException {
        if(isbn == null) throw new IllegalArgumentException();
        try {
            return DAOBook.get().getByISBN(isbn);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<Book> findBooks(int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBook.get().find(skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<Book> findBooksByTitle(String title, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBook.get().findByTitle(title, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<Book> findBooksByAuthor(String author, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBook.get().findByAuthor(author, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(BookLoan bookLoan) throws DatabaseException {
        try {
            if(bookLoan == null) throw new IllegalArgumentException();
            if(DAOBookLoan.get().exists(bookLoan)) throw new BookLoanAlreadyExistsException();
            DAOBookLoan.get().add(bookLoan);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(BookLoan... bookLoans) throws DatabaseException {
        try {
            if(bookLoans == null) throw new IllegalArgumentException();
            DAOBookLoan.get().add(bookLoans);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void update(BookLoan bookLoan) throws DatabaseException {
        try {
            if(bookLoan == null) throw new IllegalArgumentException();
            if(!DAOBookLoan.get().exists(bookLoan)) throw new BookLoanNotFoundException();
            DAOBookLoan.get().update(bookLoan);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void delete(BookLoan bookLoan) throws DatabaseException {
        try {
            if(bookLoan == null) throw new IllegalArgumentException();
            if(!DAOBookLoan.get().exists(bookLoan)) throw new BookLoanNotFoundException();
            DAOBookLoan.get().delete(bookLoan);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void deleteBookLoanBy(UserAccount user) throws DatabaseException {
        try {
            if(user == null) throw new IllegalArgumentException();
            DAOBookLoan.get().deleteBy(user);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void deleteBookLoanBy(Book book) throws DatabaseException {
        try {
            if(book == null) throw new IllegalArgumentException();
            DAOBookLoan.get().deleteBy(book);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoans(int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findAll();
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansBy(UserAccount user, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findBy(user, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansBy(Book book, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findBy(book, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansByLogin(String login, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findByLogin(login, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansByBookTitle(String title, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findByBookTitle(title, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansByBookAuthor(String author, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findByBookAuthor(author, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansByDate(LocalDate loanDate, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findByDate(loanDate, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansByDateRange(LocalDate startDate, LocalDate endDate, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findByDateRange(startDate, endDate, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookLoan> findBookLoansByPending(int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookLoan.get().findByPending(skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(BookStock bookStock) throws DatabaseException {
        try {
            if(bookStock == null) throw new IllegalArgumentException();
            if(DAOBookStock.get().exists(bookStock)) throw new BookStockAlreadyExistsException();
            DAOBookStock.get().add(bookStock);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void create(BookStock... bookStocks) throws DatabaseException {
        try {
            if(bookStocks == null) throw new IllegalArgumentException();
            DAOBookStock.get().add(bookStocks);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void update(BookStock bookStock) throws DatabaseException {
        try {
            if(bookStock == null) throw new IllegalArgumentException();
            if(!DAOBookStock.get().exists(bookStock)) throw new BookStockNotFoundException();
            DAOBookStock.get().update(bookStock);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void delete(BookStock bookStock) throws DatabaseException {
        try {
            if(bookStock == null) throw new IllegalArgumentException();
            if(!DAOBookStock.get().exists(bookStock)) throw new BookStockNotFoundException();
            DAOBookStock.get().delete(bookStock);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void deleteBookStockBy(Library library) throws DatabaseException {
        try {
            if(library == null) throw new IllegalArgumentException();
            DAOBookStock.get().deleteBy(library);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void deleteBookStockBy(Book book) throws DatabaseException {
        try {
            if(book == null) throw new IllegalArgumentException();
            DAOBookStock.get().deleteBy(book);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public Optional<BookStock> getBookStock(long libraryId, long bookId) throws DatabaseException {
        if(libraryId < 0 || bookId < 0) throw new IllegalArgumentException();
        try {
            return DAOBookStock.get().get(libraryId, bookId);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookStock> findBookStocksBy(Library library, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookStock.get().findBy(library, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookStock> findBookStocksBy(Book book, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookStock.get().findBy(book, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookStock> findBookStocksByBookTitle(String title, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookStock.get().findByBookTitle(title, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public List<BookStock> findBookStocksByBookAuthor(String author, int skip, int limit) throws DatabaseException {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        try {
            return DAOBookStock.get().findByBookAuthor(author, skip, limit);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }
}
