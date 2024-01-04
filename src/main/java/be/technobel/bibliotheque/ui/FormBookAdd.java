package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.ui.helpers.CurrentLibrary;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.DialogResult;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FormBookAdd extends Dialog<DialogResult<BookStock>> {
    private final TextField isbn;
    private final TextField title;
    private final TextField author;
    private final DatePicker publicationDate;
    private final Spinner<Integer> quantity;
    private final Label labelError;

    public FormBookAdd(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Ajout d'un livre");

        // Création des champs du formulaire
        isbn = new TextField();
        isbn.setPromptText("9781234567897");
        title = new TextField();
        author = new TextField();
        publicationDate = new DatePicker();
        quantity = new Spinner<>(0, Integer.MAX_VALUE, 1, 1);
        labelError = new Label();
        labelError.getStyleClass().add("label-error");
        labelError.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        // Création du layout du formulaire
        getDialogPane().setContent(NodeBuilder.vbox(initForm(), labelError));

        // Gestion de la validation du formulaire
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        setResultConverter(buttonType -> null);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            if(!Book.verifyISBN(isbn.getText())) {
                labelError.setText("L'ISBN est invalide.");
                return;
            }
            if(title.getText().isBlank()) {
                labelError.setText("Le titre ne peut pas être vide.");
                return;
            }
            if(author.getText().isBlank()) {
                labelError.setText("L'auteur ne peut pas être vide.");
                return;
            }
            try {
                var bookQueryResult = DBInstance.get().getBookByISBN(isbn.getText());
                if(bookQueryResult.isPresent()) {
                    labelError.setText("Cet ISBN est déjà présent dans la base de données.");
                    return;
                }
                var newBook = new Book(isbn.getText(), title.getText(), author.getText(), publicationDate.getValue());
                DBInstance.get().create(newBook);
                newBook = DBInstance.get().getBookByISBN(newBook.getISBN()).orElseThrow();
                var newStock = new BookStock(CurrentLibrary.get(), newBook, quantity.getValue());
                DBInstance.get().create(newStock);
                Message.BOOK_ADD_SUCCESS.showAndWait();
                setResult(new DialogResult<>(DialogResult.Type.ADD, newStock));
                close();
            } catch (Database.BookAlreadyExistsException | Database.BookStockAlreadyExistsException e) {
                Message.BOOK_ALREADY_EXISTS.showAndWait();
            } catch (Database.BookNoRemainingIdException e) {
                Message.DB_FULL.showAndWait();
            } catch (Database.DatabaseException | NoSuchElementException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
    }

    private GridPane initForm() {
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("ISBN"), isbn);
        grid.addRow(1, new Label("Titre"), title);
        grid.addRow(2, new Label("Auteur"), author);
        grid.addRow(3, new Label("Date de publication"), publicationDate);
        grid.addRow(4, new Label("Stock"), quantity);
        return grid;
    }

    public Optional<DialogResult<BookStock>> display() {
        isbn.clear();
        title.clear();
        author.clear();
        publicationDate.setValue(LocalDate.now());
        quantity.getValueFactory().setValue(0);
        labelError.setText("");
        return showAndWait();
    }
}
