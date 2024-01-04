package be.technobel.bibliotheque.db.mock;

import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.Library;
import be.technobel.bibliotheque.model.auth.UserAccount;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MockDB implements Database {
    private final List<UserAccount> dbUsers = new ArrayList<>();
    private long dbUsersIdCounter = 0;
    private final List<Library> dbLibraries = new ArrayList<>();
    private long dbLibrariesIdCounter = 0;
    private final List<Book> dbBooks = new ArrayList<>();
    private long dbBooksIdCounter = 0;
    private final List<BookLoan> dbBookLoans = new ArrayList<>();
    private final List<BookStock> dbBookStocks = new ArrayList<>();

    @Override
    public void close() {
        dbUsers.clear();
        dbLibraries.clear();
        dbBooks.clear();
        dbBookLoans.clear();
        dbBookStocks.clear();
        dbUsersIdCounter = 0;
        dbLibrariesIdCounter = 0;
        dbBooksIdCounter = 0;
    }

    @Override
    public void create(UserAccount user) throws UserAlreadyExistsException, UserNoRemainingIdException {
        if(user == null) throw new IllegalArgumentException();
        if(dbUsersIdCounter == Long.MAX_VALUE) throw new UserNoRemainingIdException();
        var queryResult = dbUsers.stream().filter(u -> u.getLogin().equals(user.getLogin())).findFirst();
        if(queryResult.isPresent()) throw new UserAlreadyExistsException();
        user.setId(++dbUsersIdCounter);
        dbUsers.add(user.clone());
    }

    @Override
    public void create(UserAccount... users) throws DatabaseException {
        for(var row : users) create(row);
    }

    @Override
    public void update(UserAccount user) throws UserNotFoundException {
        if(user == null) throw new IllegalArgumentException();
        var queryResult = dbUsers.stream().filter(u -> u.getId() == user.getId()).findFirst();
        if(queryResult.isEmpty()) throw new UserNotFoundException();
        dbUsers.set(dbUsers.indexOf(queryResult.get()), user.clone());
    }

    @Override
    public void delete(UserAccount user) throws UserNotFoundException {
        if(user == null) throw new IllegalArgumentException();
        if(!dbUsers.removeIf(u -> u.getId() == user.getId())) throw new UserNotFoundException();
    }

    @Override
    public Optional<UserAccount> getUser(long id) {
        if(id < 0) throw new IllegalArgumentException();
        return dbUsers.stream().filter(u -> u.getId() == id).map(UserAccount::clone).findFirst();
    }

    @Override
    public Optional<UserAccount> getUserByLogin(String login) {
        if(login == null || login.isBlank()) throw new IllegalArgumentException();
        return dbUsers.stream().filter(u -> u.getLogin().equals(login)).map(UserAccount::clone).findFirst();
    }

    @Override
    public void create(Library library) throws LibraryAlreadyExistsException, LibraryNoRemainingIdException {
        if(library == null) throw new IllegalArgumentException();
        if(dbLibrariesIdCounter == Long.MAX_VALUE) throw new LibraryNoRemainingIdException();
        var queryResult = dbLibraries.stream().filter(l -> l.getId() == library.getId()).findFirst();
        if(queryResult.isPresent()) throw new LibraryAlreadyExistsException();
        library.setId(++dbLibrariesIdCounter);
        dbLibraries.add(library.clone());
    }

    @Override
    public void update(Library library) throws LibraryNotFoundException {
        if(library == null) throw new IllegalArgumentException();
        var queryResult = dbLibraries.stream().filter(l -> l.getId() == library.getId()).findFirst();
        if(queryResult.isEmpty()) throw new LibraryNotFoundException();
        dbLibraries.set(dbLibraries.indexOf(queryResult.get()), library.clone());
    }

    @Override
    public void delete(Library library) throws LibraryNotFoundException {
        if(library == null) throw new IllegalArgumentException();
        if(!dbLibraries.removeIf(l -> l.getId() == library.getId())) throw new LibraryNotFoundException();
    }

    @Override
    public Optional<Library> getLibrary(long id) {
        if(id < 0) throw new IllegalArgumentException();
        return dbLibraries.stream().filter(l -> l.getId() == id).map(Library::clone).findFirst();
    }

    @Override
    public void create(Book book) throws BookAlreadyExistsException, BookNoRemainingIdException {
        if(book == null) throw new IllegalArgumentException();
        if(dbBooksIdCounter == Long.MAX_VALUE) throw new BookNoRemainingIdException();
        var queryResult = dbBooks.stream().filter(b -> b.getISBN().equals(book.getISBN())).findFirst();
        if(queryResult.isPresent()) throw new BookAlreadyExistsException();
        book.setId(++dbBooksIdCounter);
        dbBooks.add(book.clone());
    }

    @Override
    public void create(Book... books) throws DatabaseException {
        for(var row : books) create(row);
    }

    @Override
    public void update(Book book) throws BookNotFoundException {
        if(book == null) throw new IllegalArgumentException();
        var queryResult = dbBooks.stream().filter(b -> b.getId() == book.getId()).findFirst();
        if(queryResult.isEmpty()) throw new BookNotFoundException();
        dbBooks.set(dbBooks.indexOf(queryResult.get()), book.clone());
    }

    @Override
    public void delete(Book book) throws BookNotFoundException {
        if(book == null) throw new IllegalArgumentException();
        if(!dbBooks.removeIf(b -> b.getId() == book.getId())) throw new BookNotFoundException();
    }

    @Override
    public Optional<Book> getBook(long id) {
        if(id < 0) throw new IllegalArgumentException();
        return dbBooks.stream().filter(b -> b.getId() == id).map(Book::clone).findFirst();
    }

    @Override
    public Optional<Book> getBookByISBN(String isbn) {
        if(!Book.verifyISBN(isbn)) throw new IllegalArgumentException();
        return dbBooks.stream().filter(b -> b.getISBN().equals(isbn)).map(Book::clone).findFirst();
    }

    @Override
    public List<Book> findBooks(int skip, int limit) {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBooks.stream().sorted(Comparator.comparing(Book::getTitle));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(Book::clone).collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByTitle(String title, int skip, int limit) {
        if(title == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBooks.stream()
            .filter(b -> b.getTitle().contains(title))
            .sorted(Comparator.comparing(Book::getTitle));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(Book::clone).collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByAuthor(String author, int skip, int limit) {
        if(author == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBooks.stream()
            .filter(b -> b.getAuthor().contains(author))
            .sorted(Comparator.comparing(Book::getAuthor));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(Book::clone).collect(Collectors.toList());
    }

    @Override
    public void create(BookLoan bookLoan) throws BookLoanAlreadyExistsException {
        if(bookLoan == null) throw new IllegalArgumentException();
        var queryResult = dbBookLoans.stream().filter(bl ->
            bl.getUser().getLogin().equals(bookLoan.getUser().getLogin()) &&
            bl.getBook().getISBN().equals(bookLoan.getBook().getISBN()) &&
            bl.getLoanDate().equals(bookLoan.getLoanDate())
        ).findFirst();
        if(queryResult.isPresent()) throw new BookLoanAlreadyExistsException();
        dbBookLoans.add(bookLoan.clone());
    }

    @Override
    public void create(BookLoan... bookLoans) throws DatabaseException {
        for(var row : bookLoans) create(row);
    }

    @Override
    public void update(BookLoan bookLoan) throws BookLoanNotFoundException {
        if(bookLoan == null) throw new IllegalArgumentException();
        var queryResult = dbBookLoans.stream().filter(bl ->
            bl.getUser().getLogin().equals(bookLoan.getUser().getLogin()) &&
            bl.getBook().getISBN().equals(bookLoan.getBook().getISBN()) &&
            bl.getLoanDate().equals(bookLoan.getLoanDate())
        ).findFirst();
        if(queryResult.isEmpty()) throw new BookLoanNotFoundException();
        dbBookLoans.set(dbBookLoans.indexOf(queryResult.get()), bookLoan.clone());
    }

    @Override
    public void delete(BookLoan bookLoan) throws BookLoanNotFoundException {
        if(bookLoan == null) throw new IllegalArgumentException();
        if(!dbBookLoans.removeIf(bl ->
            bl.getUser().getLogin().equals(bookLoan.getUser().getLogin()) &&
            bl.getBook().getISBN().equals(bookLoan.getBook().getISBN()) &&
            bl.getLoanDate().equals(bookLoan.getLoanDate())
        )) throw new BookLoanNotFoundException();
    }

    @Override
    public void deleteBookLoanBy(UserAccount user) {
        dbBookLoans.removeIf(bl -> bl.getUser().getId() == user.getId());
    }

    @Override
    public void deleteBookLoanBy(Book book) {
        dbBookLoans.removeIf(bl -> bl.getBook().getId() == book.getId());
    }

    @Override
    public List<BookLoan> findBookLoans(int skip, int limit) {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream().sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansBy(UserAccount user, int skip, int limit) {
        if(user == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> bl.getUser().getId() == user.getId())
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansBy(Book book, int skip, int limit) {
        if(book == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> bl.getBook().getId() == book.getId())
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansByLogin(String login, int skip, int limit) {
        if(login == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> bl.getUser().getLogin().contains(login))
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansByBookTitle(String title, int skip, int limit) {
        if(title == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> bl.getBook().getTitle().contains(title))
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansByBookAuthor(String author, int skip, int limit) {
        if(author == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> bl.getBook().getAuthor().contains(author))
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansByDate(LocalDate loanDate, int skip, int limit) {
        if(loanDate == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> bl.getLoanDate().isEqual(loanDate))
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansByDateRange(LocalDate startDate, LocalDate endDate, int skip, int limit) {
        if(startDate == null || endDate == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(bl -> !bl.getLoanDate().isBefore(startDate) && !bl.getLoanDate().isAfter(startDate))
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookLoan> findBookLoansByPending(int skip, int limit) {
        if(skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookLoans.stream()
            .filter(BookLoan::isPending)
            .sorted(Comparator.comparing(BookLoan::getLoanDate));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookLoan::clone).collect(Collectors.toList());
    }

    @Override
    public void create(BookStock bookStock) throws BookStockAlreadyExistsException {
        if(bookStock == null) throw new IllegalArgumentException();
        var queryResult = dbBookStocks.stream().filter(bs ->
            bs.getLibrary().getId() == bookStock.getLibrary().getId() &&
            bs.getBook().getId() == bookStock.getBook().getId()
        ).findFirst();
        if(queryResult.isPresent()) throw new BookStockAlreadyExistsException();
        dbBookStocks.add(bookStock.clone());
    }

    @Override
    public void create(BookStock... bookStocks) throws DatabaseException {
        for(var row : bookStocks) create(row);
    }

    @Override
    public void update(BookStock bookStock) throws BookStockNotFoundException {
        if(bookStock == null) throw new IllegalArgumentException();
        var queryResult = dbBookStocks.stream().filter(bs ->
            bs.getLibrary().getId() == bookStock.getLibrary().getId() &&
            bs.getBook().getId() == bookStock.getBook().getId()
        ).findFirst();
        if(queryResult.isEmpty()) throw new BookStockNotFoundException();
        dbBookStocks.set(dbBookStocks.indexOf(queryResult.get()), bookStock.clone());
    }

    @Override
    public void delete(BookStock bookStock) throws BookStockNotFoundException {
        if(bookStock == null) throw new IllegalArgumentException();
        if(!dbBookStocks.removeIf(bs ->
            bs.getLibrary().getId() == bookStock.getLibrary().getId() &&
            bs.getBook().getId() == bookStock.getBook().getId()
        )) throw new BookStockNotFoundException();
    }

    @Override
    public void deleteBookStockBy(Library library) {
        dbBookStocks.removeIf(bs -> bs.getLibrary().getId() == library.getId());
    }

    @Override
    public void deleteBookStockBy(Book book) {
        dbBookStocks.removeIf(bs -> bs.getBook().getId() == book.getId());
    }

    @Override
    public Optional<BookStock> getBookStock(long libraryId, long bookId) {
        if(libraryId < 0 || bookId < 0) throw new IllegalArgumentException();
        return this.dbBookStocks.stream().filter(bl ->
            bl.getLibrary().getId() == libraryId && bl.getBook().getId() == bookId
        ).map(BookStock::clone).findFirst();
    }

    @Override
    public List<BookStock> findBookStocksBy(Library library, int skip, int limit) {
        if(library == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookStocks.stream().filter(bs -> bs.getLibrary().getId() == library.getId());
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookStock::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookStock> findBookStocksBy(Book book, int skip, int limit) {
        if(book == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookStocks.stream().filter(bs -> bs.getBook().getId() == book.getId());
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookStock::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookStock> findBookStocksByBookTitle(String title, int skip, int limit) {
        if(title == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookStocks.stream()
            .filter(bs -> bs.getBook().getTitle().contains(title))
            .sorted(Comparator.comparing(bs -> bs.getBook().getTitle()));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookStock::clone).collect(Collectors.toList());
    }

    @Override
    public List<BookStock> findBookStocksByBookAuthor(String author, int skip, int limit) {
        if(author == null || skip < 0 || limit < 0) throw new IllegalArgumentException();
        var stream = this.dbBookStocks.stream()
            .filter(bs -> bs.getBook().getAuthor().contains(author))
            .sorted(Comparator.comparing(bs -> bs.getBook().getAuthor()));
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(BookStock::clone).collect(Collectors.toList());
    }
}
