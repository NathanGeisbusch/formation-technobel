package be.technobel.bibliotheque.db.mock;

import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.db.sql.DAO;
import be.technobel.bibliotheque.db.sql.SQLiteDB;
import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.Address;
import be.technobel.bibliotheque.model.auth.Library;
import be.technobel.bibliotheque.model.auth.UserAccount;
import be.technobel.bibliotheque.model.auth.UserRole;
import java.time.LocalDate;

public class Mock {
    public static void populateDatabase(Database db) {
        try {
            if(db instanceof SQLiteDB) DAO.SQL_CONNECTION.get().setAutoCommit(false);
            final var LIBRARIES = new Library[] {
                new Library("Stack Underflow", new Address(
                    "Belgique", "Bruxelles", "1000", "Place de la Liberté", "123"
                ))
            };
            for(var library : LIBRARIES) db.create(library);
            for(int i = 0; i < LIBRARIES.length; i++) LIBRARIES[i] = db.getLibrary(i+1).orElseThrow();

            final var USERS = new UserAccount[] {
                new UserAccount("admin", "admin", UserRole.ADMIN, "Administrateur", "",
                    LocalDate.parse("1970-01-01"), new Address(
                    "Belgique", "Anvers", "2000", "Kloosterstraat", "45"
                )),
                new UserAccount("bill", "ding", UserRole.USER, "Ding", "Bill",
                    LocalDate.parse("1975-03-20"), new Address(
                    "Belgique", "Bruges", "8000", "Wollestraat", "7"
                )),
                new UserAccount("brock", "lee", UserRole.USER, "Lee", "Brock",
                    LocalDate.parse("1984-08-03"), new Address(
                    "Belgique", "Gand", "9000", "Veldstraat", "78"
                )),
                new UserAccount("anna", "gramme", UserRole.USER, "Gramme", "Anna",
                    LocalDate.parse("1991-10-01"), new Address(
                    "Belgique", "Liège", "4000", "Rue Léopold", "56"
                )),
            };
            db.create(USERS);
            for(int i = 0; i < USERS.length; i++) USERS[i] = db.getUser(i+1).orElseThrow();

            final Book[] BOOKS = new Book[] {
                new Book("9781705309902", "Beej's Guide to Network Programming: Using Internet Sockets",
                        "Brian R. Hall", LocalDate.parse("2019-12-12")),
                new Book("9798697247372", "Assembly Language Programming with Ubuntu",
                        "Ed Jorgensen", LocalDate.parse("2020-12-27")),
                new Book("9780136807292 ", "Multithreaded Programming With Pthreads",
                        "Bil Lewis", LocalDate.parse("1997-01-01")),
                new Book("9780201633924", "Programming with POSIX Threads",
                        "Butenhof David", LocalDate.parse("1997-08-08")),
                new Book("9780072232158", "C++: A Beginner's Guide, Second Edition",
                        "Herbert Schildt", LocalDate.parse("2003-12-01")),
                new Book("9780470184271", "C++ pour les nuls",
                        "Dan Gookin", LocalDate.parse("1994-01-01")),
                new Book("9781491922064", "Optimized C++: Proven Techniques for Heightened Performance",
                        "Kurt Guntheroth", LocalDate.parse("2016-06-14")),
                new Book("9781783280278", "C++ GUI Programming Cookbook: Design and build a functional, appealing, and user-friendly graphical user interface",
                        "Lee Zhi Eng", LocalDate.parse("2016-07-29")),
                new Book("9781304661050", "The Rook's Guide to C++",
                        "Jeremy Hansen", LocalDate.parse("2013-12-13")),
                new Book("9789090332567", "Learn OpenGL: Learn modern OpenGL graphics programming in a step-by-step fashion",
                        "Joey de Vries", LocalDate.parse("2020-06-17")),
                new Book("9780134495491", "OpenGL Programming Guide: The Official Guide to Learning OpenGL",
                        "John Kessenich", LocalDate.parse("2016-07-08")),
                new Book("9780672323089", "Object-Oriented Programming in C++ (4th Edition)",
                        "Robert Lafore", LocalDate.parse("2002-01-04")),
                new Book("9781927356388", "Open Data Structures: An Introduction",
                        "Pat Morin", LocalDate.parse("2013-06-01")),
                new Book("9781590597934", "Foundations of GTK+ Development",
                        "Andrew Krause", LocalDate.parse("2007-01-01")),
                new Book("9780134464541", "Vulkan Programming Guide: The Official Guide to Learning Vulkan",
                        "Sellers Graham", LocalDate.parse("2016-10-28")),
                new Book("9781593278281", "The Rust Programming Language",
                        "Steve Klabnik", LocalDate.parse("2018-06-26")),
                new Book("9780596521189", "Using SQLite: Small. Fast. Reliable. Choose Any Three.",
                        "Jay Kreibich", LocalDate.parse("2010-09-21")),
                new Book("9781680506952", "Programming Flutter: Native, Cross-Platform Apps the Easy Way",
                        "Carmine Zaccagnino", LocalDate.parse("2020-03-31")),
                new Book("9798836539412", "Mastering Nim: A complete guide to the programming language ",
                        "Andreas Rumpf", LocalDate.parse("2022-06-22")),
                new Book("9780134190440", "Go Programming Language",
                        "Alan Donovan", LocalDate.parse("2015-10-26")),
                new Book("9780521692694", "Programming in Haskell",
                        "Graham Hutton", LocalDate.parse("2007-01-15")),
                new Book("9780321349804", "The Java Programming Language, 4th Edition",
                        "Ken Arnold", LocalDate.parse("2005-08-15")),
                new Book("9780135161630", "Kotlin Programming: The Big Nerd Ranch Guide",
                        "Josh Skeen", LocalDate.parse("2018-09-13")),
                new Book("9781484253793", "Introducing Vala Programming: A Language and Techniques to Boost Productivity",
                        "Michael Lauer", LocalDate.parse("2019-11-07")),
            };
            db.create(BOOKS);
            for(int i = 0; i < BOOKS.length; i++) BOOKS[i] = db.getBook(i+1).orElseThrow();

            final BookStock[] BOOK_STOCKS = new BookStock[] {
                new BookStock(LIBRARIES[0], BOOKS[0], 12),
                new BookStock(LIBRARIES[0], BOOKS[1], 3),
                new BookStock(LIBRARIES[0], BOOKS[2], 6),
                new BookStock(LIBRARIES[0], BOOKS[3], 7),
                new BookStock(LIBRARIES[0], BOOKS[4], 20),
                new BookStock(LIBRARIES[0], BOOKS[5], 8),
                new BookStock(LIBRARIES[0], BOOKS[6], 5),
                new BookStock(LIBRARIES[0], BOOKS[7], 3),
                new BookStock(LIBRARIES[0], BOOKS[8], 2),
                new BookStock(LIBRARIES[0], BOOKS[9], 4),
                new BookStock(LIBRARIES[0], BOOKS[10], 5),
                new BookStock(LIBRARIES[0], BOOKS[11], 2),
                new BookStock(LIBRARIES[0], BOOKS[12], 1),
                new BookStock(LIBRARIES[0], BOOKS[13], 2),
                new BookStock(LIBRARIES[0], BOOKS[14], 4),
                new BookStock(LIBRARIES[0], BOOKS[15], 3),
                new BookStock(LIBRARIES[0], BOOKS[16], 4),
                new BookStock(LIBRARIES[0], BOOKS[17], 5),
                new BookStock(LIBRARIES[0], BOOKS[18], 1),
                new BookStock(LIBRARIES[0], BOOKS[19], 5),
                new BookStock(LIBRARIES[0], BOOKS[20], 1),
                new BookStock(LIBRARIES[0], BOOKS[21], 5),
                new BookStock(LIBRARIES[0], BOOKS[22], 7),
                new BookStock(LIBRARIES[0], BOOKS[23], 3),
            };
            db.create(BOOK_STOCKS);

            final BookLoan[] BOOK_LOANS = new BookLoan[] {
                new BookLoan(USERS[1], BOOKS[0], LocalDate.parse("2020-01-02"), LocalDate.parse("2020-01-26"), 1, 1),
                new BookLoan(USERS[0], BOOKS[0], LocalDate.parse("2020-01-03"), LocalDate.parse("2020-01-27"), 1, 1),
                new BookLoan(USERS[1], BOOKS[2], LocalDate.parse("2020-01-05"), LocalDate.parse("2020-01-25"), 1, 1),
                new BookLoan(USERS[2], BOOKS[1], LocalDate.parse("2020-01-08"), LocalDate.parse("2020-01-22"), 1, 1),
                new BookLoan(USERS[0], BOOKS[6], LocalDate.parse("2020-01-15"), LocalDate.parse("2020-02-15"), 1, 1),
                new BookLoan(USERS[1], BOOKS[5], LocalDate.parse("2020-01-17"), LocalDate.parse("2020-02-13"), 1, 1),
                new BookLoan(USERS[2], BOOKS[7], LocalDate.parse("2020-01-20"), LocalDate.parse("2020-02-10"), 1, 1),
                new BookLoan(USERS[0], BOOKS[4], LocalDate.parse("2020-03-02"), LocalDate.parse("2020-03-28"), 1, 1),
                new BookLoan(USERS[1], BOOKS[3], LocalDate.parse("2020-05-07"), LocalDate.parse("2020-05-23"), 1, 1),
                new BookLoan(USERS[2], BOOKS[2], LocalDate.parse("2020-08-09"), LocalDate.parse("2020-08-21"), 1, 1),
                new BookLoan(USERS[3], BOOKS[11], LocalDate.parse("2020-12-21"), LocalDate.parse("2021-01-12"), 1, 1),
                new BookLoan(USERS[3], BOOKS[10], LocalDate.parse("2022-11-12"), LocalDate.parse("2022-12-11"), 1, 1),
                new BookLoan(USERS[3], BOOKS[9], LocalDate.parse("2023-10-01"), LocalDate.parse("2023-11-11"), 1, 1),
                new BookLoan(USERS[0], BOOKS[14], LocalDate.parse("2023-09-02"), null, 1, 0),
                new BookLoan(USERS[1], BOOKS[19], LocalDate.parse("2023-09-20"), null, 1, 0),
            };
            db.create(BOOK_LOANS);
            if(db instanceof SQLiteDB) DAO.SQL_CONNECTION.get().setAutoCommit(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
