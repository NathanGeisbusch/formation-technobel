package be.technobel.bibliotheque.model;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Book implements Cloneable {
    public static final Pattern REGEX_ISBN_13 = Pattern.compile("^97[89]\\d{10}$");
    private static final Pattern REGEX_ISBN_DELIMITERS = Pattern.compile("[- ]");

    private long id = 0;
    private final String isbn;
    private String title;
    private String author;
    private LocalDate publicationDate;

    public long getId() {
        if(id < 0) throw new IllegalArgumentException();
        return id;
    }

    public Book setId(long id) {
        this.id = id;
        return this;
    }

    public String getISBN() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Book setTitle(String title) {
        if(title == null || title.isBlank()) throw new IllegalArgumentException();
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Book setAuthor(String author) {
        if(author == null || author.isBlank()) throw new IllegalArgumentException();
        this.author = author;
        return this;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public Book setPublicationDate(LocalDate publicationDate) {
        if(publicationDate == null) throw new IllegalArgumentException();
        this.publicationDate = publicationDate;
        return this;
    }

    public Book(long id, String isbn, String title, String author, LocalDate publicationDate) {
        if(isbn == null || title == null || author == null || publicationDate == null)
            throw new IllegalArgumentException();
        String normalizedISBN = REGEX_ISBN_DELIMITERS.matcher(isbn.trim()).replaceAll("");
        if(id < 0 || !verifyISBN(normalizedISBN) || title.isBlank() || author.isBlank())
            throw new IllegalArgumentException();
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.isbn = normalizedISBN;
    }

    public Book(String isbn, String title, String author, LocalDate publicationDate) {
        if(isbn == null || title == null || author == null || publicationDate == null)
            throw new IllegalArgumentException();
        String normalizedISBN = REGEX_ISBN_DELIMITERS.matcher(isbn.trim()).replaceAll("");
        if(!verifyISBN(normalizedISBN) || title.isBlank()) throw new IllegalArgumentException();
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.isbn = normalizedISBN;
    }

    /**
     * Vérifie un ISBN de 13 chiffes.
     * @param isbn ISBN de 13 chiffes (sans tiret ni espace)
     * @return true si l'ISBN est valide
     */
    public static boolean verifyISBN(String isbn) {
        if(isbn == null || isbn.isBlank()) return false;
        if(REGEX_ISBN_13.matcher(isbn).matches()) {
            int sum = 0;
            int digitCounter = 0;
            for(int i = 0; i < 12; i++) {
                int digit = isbn.charAt(i)-'0';
                int multiplier = digitCounter%2 == 0 ? 1 : 3;
                sum += digit * multiplier;
                ++digitCounter;
            }
            int lastDigit = isbn.charAt(12)-'0';
            return (10-(sum%10))%10 == lastDigit;
        }
        return false;
    }

    @Override
    public Book clone() {
        try {
            return (Book)super.clone();
        }
        catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
