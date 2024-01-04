package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.Book;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.ui.helpers.CurrentLibrary;
import be.technobel.bibliotheque.ui.helpers.DialogResult;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;

public class FormBookUpdate extends Dialog<DialogResult<BookStock>> {
    private final TextField isbn;
    private final TextField title;
    private final TextField author;
    private final DatePicker publicationDate;
    private final Spinner<Integer> quantity;
    private final Label labelError;
    private BookStock bookStock;

    public FormBookUpdate(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Modification d'un livre");

        // Création des champs du formulaire
        isbn = new TextField();
        isbn.setEditable(false);
        isbn.setDisable(true);
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
            var newBook = new Book(bookStock.getBook().getId(), isbn.getText(), title.getText(), author.getText(), publicationDate.getValue());
            var newStock = new BookStock(CurrentLibrary.get(), newBook, quantity.getValue());
            try {
                DBInstance.get().update(newBook);
                DBInstance.get().update(newStock);
                Message.BOOK_UPDATE_SUCCESS.showAndWait();
                setResult(new DialogResult<>(DialogResult.Type.UPDATE, newStock));
                close();
            } catch (Database.DatabaseException e) {
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

    public Optional<DialogResult<BookStock>> display(BookStock bookStock) {
        isbn.setText(bookStock.getBook().getISBN());
        title.setText(bookStock.getBook().getTitle());
        author.setText(bookStock.getBook().getAuthor());
        publicationDate.setValue(bookStock.getBook().getPublicationDate());
        quantity.getValueFactory().setValue(bookStock.getQuantity());
        labelError.setText("");
        this.bookStock = bookStock;
        return showAndWait();
    }
}