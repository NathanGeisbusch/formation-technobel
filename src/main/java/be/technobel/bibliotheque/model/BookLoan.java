package be.technobel.bibliotheque.model;

import be.technobel.bibliotheque.model.auth.UserAccount;
import java.time.LocalDate;

public class BookLoan implements Cloneable {
    private UserAccount user;
    private Book book;
    private final LocalDate loanDate;
    private LocalDate returnDate;
    private int loanQuantity;
    private int returnQuantity;

    public UserAccount getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BookLoan setReturnDate(LocalDate returnDate) {
        if(returnDate != null && loanDate.isAfter(returnDate)) throw new IllegalArgumentException();
        this.returnDate = returnDate;
        return this;
    }

    public int getLoanQuantity() {
        return loanQuantity;
    }

    public BookLoan setLoanQuantity(int loanQuantity) {
        if(loanQuantity < 1 || returnQuantity > loanQuantity) throw new IllegalArgumentException();
        this.loanQuantity = loanQuantity;
        return this;
    }

    public int getReturnQuantity() {
        return returnQuantity;
    }

    public BookLoan setReturnQuantity(int returnQuantity) {
        if(returnQuantity < 0 || returnQuantity > loanQuantity) throw new IllegalArgumentException();
        this.returnQuantity = returnQuantity;
        return this;
    }

    /**
     * @param returnDate la valeur peut être null pour indiquer que le livre n'a pas encore été rendu
     */
    public BookLoan(UserAccount user, Book book, LocalDate loanDate, LocalDate returnDate, int loanQuantity, int returnQuantity) {
        if(user == null || book == null || loanDate == null) throw new IllegalArgumentException();
        if((returnDate != null && loanDate.isAfter(returnDate)) ||
        loanQuantity < 1 || returnQuantity < 0 || returnQuantity > loanQuantity)
            throw new IllegalArgumentException();
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.loanQuantity = loanQuantity;
        this.returnQuantity = returnQuantity;
    }

    public boolean isPending() {
        return loanQuantity != returnQuantity;
    }

    @Override
    public BookLoan clone() {
        try {
            var clone = (BookLoan)super.clone();
            clone.user = clone.user.clone();
            clone.book = clone.book.clone();
            return clone;
        }
        catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
