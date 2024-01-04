package be.technobel.bibliotheque.db;

import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.Library;
import be.technobel.bibliotheque.model.auth.UserAccount;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface Database {
    class DatabaseException extends Exception {}
    class AddressNoRemainingIdException extends DatabaseException {}
    class UserNoRemainingIdException extends DatabaseException {}
    class UserAlreadyExistsException extends DatabaseException {}
    class UserNotFoundException extends DatabaseException {}
    class LibraryNoRemainingIdException extends DatabaseException {}
    class LibraryAlreadyExistsException extends DatabaseException {}
    class LibraryNotFoundException extends DatabaseException {}
    class BookNoRemainingIdException extends DatabaseException {}
    class BookAlreadyExistsException extends DatabaseException {}
    class BookNotFoundException extends DatabaseException {}
    class BookLoanAlreadyExistsException extends DatabaseException {}
    class BookLoanNotFoundException extends DatabaseException {}
    class BookStockAlreadyExistsException extends DatabaseException {}
    class BookStockNotFoundException extends DatabaseException {}
    void close() throws DatabaseException;

    void create(UserAccount user) throws DatabaseException;
    void create(UserAccount... users) throws DatabaseException;
    void update(UserAccount user) throws DatabaseException;
    void delete(UserAccount user) throws DatabaseException;
    Optional<UserAccount> getUser(long id) throws DatabaseException;
    Optional<UserAccount> getUserByLogin(String login) throws DatabaseException;

    void create(Library library) throws DatabaseException;
    void update(Library library) throws DatabaseException;
    void delete(Library library) throws DatabaseException;
    Optional<Library> getLibrary(long id) throws DatabaseException;

    void create(Book book) throws DatabaseException;
    void create(Book... books) throws DatabaseException;
    void update(Book book) throws DatabaseException;
    void delete(Book book) throws DatabaseException;
    Optional<Book> getBook(long id) throws DatabaseException;
    Optional<Book> getBookByISBN(String isbn) throws DatabaseException;
    List<Book> findBooks(int skip, int limit) throws DatabaseException;
    List<Book> findBooksByTitle(String title, int skip, int limit) throws DatabaseException;
    List<Book> findBooksByAuthor(String author, int skip, int limit) throws DatabaseException;

    void create(BookLoan bookLoan) throws DatabaseException;
    void create(BookLoan... bookLoans) throws DatabaseException;
    void update(BookLoan bookLoan) throws DatabaseException;
    void delete(BookLoan bookLoan) throws DatabaseException;
    void deleteBookLoanBy(UserAccount user) throws DatabaseException;
    void deleteBookLoanBy(Book book) throws DatabaseException;
    List<BookLoan> findBookLoans(int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansBy(UserAccount user, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansBy(Book book, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansByLogin(String login, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansByBookTitle(String title, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansByBookAuthor(String author, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansByDate(LocalDate loanDate, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansByDateRange(LocalDate startDate, LocalDate endDate, int skip, int limit) throws DatabaseException;
    List<BookLoan> findBookLoansByPending(int skip, int limit) throws DatabaseException;

    void create(BookStock bookStock) throws DatabaseException;
    void create(BookStock... bookStocks) throws DatabaseException;
    void update(BookStock bookStock) throws DatabaseException;
    void delete(BookStock bookStock) throws DatabaseException;
    void deleteBookStockBy(Library library) throws DatabaseException;
    void deleteBookStockBy(Book book) throws DatabaseException;
    Optional<BookStock> getBookStock(long libraryId, long bookId) throws DatabaseException;
    List<BookStock> findBookStocksBy(Library library, int skip, int limit) throws DatabaseException;
    List<BookStock> findBookStocksBy(Book book, int skip, int limit) throws DatabaseException;
    List<BookStock> findBookStocksByBookTitle(String title, int skip, int limit) throws DatabaseException;
    List<BookStock> findBookStocksByBookAuthor(String author, int skip, int limit) throws DatabaseException;
}
