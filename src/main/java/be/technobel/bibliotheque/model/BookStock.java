package be.technobel.bibliotheque.model;

import be.technobel.bibliotheque.model.auth.Library;

public class BookStock implements Cloneable {
    private Library library;
    private Book book;
    private int quantity = 0;

    public Library getLibrary() {
        return library;
    }

    public Book getBook() {
        return book;
    }

    public int getQuantity() {
        return quantity;
    }

    public BookStock setQuantity(int quantity) {
        if(quantity < 0) throw new IllegalArgumentException();
        this.quantity = quantity;
        return this;
    }

    public BookStock(Library library, Book book, int quantity) {
        if(library == null || book == null || quantity < 0) throw new IllegalArgumentException();
        this.library = library;
        this.book = book;
        this.quantity = quantity;
    }

    public BookStock(Library library, Book book) {
        if(library == null || book == null) throw new IllegalArgumentException();
        this.library = library;
        this.book = book;
    }

    @Override
    public BookStock clone() {
        try {
            var clone = (BookStock)super.clone();
            clone.library = clone.library.clone();
            clone.book = clone.book.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
