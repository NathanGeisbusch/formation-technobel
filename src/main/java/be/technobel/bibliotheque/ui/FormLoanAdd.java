package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.UserAccount;
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
import java.util.Optional;

public class FormLoanAdd extends Dialog<DialogResult<BookLoan>>  {
    private final TextField title;
    private final DatePicker loanDate;
    private final Spinner<Integer> loanQuantity;
    private UserAccount user;
    private BookStock stock;

    public FormLoanAdd(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Emprunt de livre");

        // Création des champs du formulaire
        title = new TextField();
        title.setEditable(false);
        title.setDisable(true);
        loanDate = new DatePicker();
        loanDate.setEditable(false);
        loanDate.setDisable(true);
        loanQuantity = new Spinner<>(0, Integer.MAX_VALUE, 1, 1);
        loanQuantity.setMaxWidth(Double.MAX_VALUE);

        // Création du layout du formulaire
        getDialogPane().setContent(NodeBuilder.vbox(initForm()));

        // Gestion de la validation du formulaire
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        setResultConverter(buttonType -> null);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            var newLoan = new BookLoan(
                user, stock.getBook(), loanDate.getValue(), null,
                loanQuantity.getValue(), 0
            );
            int newQuantity = stock.getQuantity() - loanQuantity.getValue();
            var newStock = new BookStock(CurrentLibrary.get(), stock.getBook(), newQuantity);
            try {
                DBInstance.get().create(newLoan);
                DBInstance.get().update(newStock);
                Message.LOAN_ADD_SUCCESS.showAndWait();
                setResult(new DialogResult<>(DialogResult.Type.ADD, newLoan));
                close();
            } catch (Database.BookLoanAlreadyExistsException e) {
                Message.LOAN_ALREADY_EXISTS.showAndWait();
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
    }

    private GridPane initForm() {
        var grid = NodeBuilder.gridFormGrow();
        grid.addRow(0, new Label("Titre du livre"), title);
        grid.addRow(1, new Label("Date d'emprunt"), loanDate);
        grid.addRow(2, new Label("Quantité empruntée"), loanQuantity);
        return grid;
    }

    public Optional<DialogResult<BookLoan>> display(UserAccount user, BookStock stock) {
        title.setText(stock.getBook().getTitle());
        loanDate.setValue(LocalDate.now());
        loanQuantity.getValueFactory().setValue(1);
        ((SpinnerValueFactory.IntegerSpinnerValueFactory)loanQuantity.getValueFactory()).setMax(stock.getQuantity());
        this.user = user;
        this.stock = stock;
        return showAndWait();
    }
}
