package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.BookLoan;
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
import java.time.LocalDate;
import java.util.Optional;

public class FormLoanUpdate extends Dialog<DialogResult<BookLoan>>  {
    private final TextField title;
    private final DatePicker loanDate;
    private final DatePicker returnDate;
    private final Spinner<Integer> loanQuantity;
    private final Spinner<Integer> returnQuantity;
    private BookLoan loan;

    public FormLoanUpdate(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Retour de livre");

        // Création des champs du formulaire
        title = new TextField();
        title.setEditable(false);
        title.setDisable(true);
        loanDate = new DatePicker();
        loanDate.setEditable(false);
        loanDate.setDisable(true);
        returnDate = new DatePicker();
        returnDate.setEditable(false);
        returnDate.setDisable(true);
        loanQuantity = new Spinner<>(0, Integer.MAX_VALUE, 1, 1);
        loanQuantity.setMaxWidth(Double.MAX_VALUE);
        loanQuantity.setEditable(false);
        loanQuantity.setDisable(true);
        returnQuantity = new Spinner<>(0, Integer.MAX_VALUE, 0, 1);
        returnQuantity.setMaxWidth(Double.MAX_VALUE);

        // Création du layout du formulaire
        getDialogPane().setContent(NodeBuilder.vbox(initForm()));

        // Gestion de la validation du formulaire
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        setResultConverter(buttonType -> null);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            var newLoan = new BookLoan(
                loan.getUser(), loan.getBook(), loanDate.getValue(), returnDate.getValue(),
                loanQuantity.getValue(), returnQuantity.getValue()
            );
            try {
                var stock = DBInstance.get().getBookStock(CurrentLibrary.get().getId(), loan.getBook().getId())
                    .orElseThrow(Database.BookStockNotFoundException::new);
                int newQuantity = stock.getQuantity() + returnQuantity.getValue() - loan.getReturnQuantity();
                var newStock = new BookStock(CurrentLibrary.get(), stock.getBook(), newQuantity);
                DBInstance.get().update(newLoan);
                DBInstance.get().update(newStock);
                Message.LOAN_UPDATE_SUCCESS.showAndWait();
                setResult(new DialogResult<>(DialogResult.Type.UPDATE, newLoan));
                close();
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
    }

    private GridPane initForm() {
        var grid = NodeBuilder.gridFormGrow();
        grid.addRow(0, new Label("Titre du livre"), title);
        grid.addRow(1, new Label("Date d'emprunt"), loanDate);
        grid.addRow(2, new Label("Date de retour"), returnDate);
        grid.addRow(3, new Label("Quantité empruntée"), loanQuantity);
        grid.addRow(4, new Label("Quantité retournée"), returnQuantity);
        return grid;
    }

    public Optional<DialogResult<BookLoan>> display(BookLoan loan) {
        title.setText(loan.getBook().getTitle());
        loanDate.setValue(loan.getLoanDate());
        returnDate.setValue(LocalDate.now());
        loanQuantity.getValueFactory().setValue(loan.getLoanQuantity());
        returnQuantity.getValueFactory().setValue(loan.getReturnQuantity());
        ((SpinnerValueFactory.IntegerSpinnerValueFactory)returnQuantity.getValueFactory()).setMax(loan.getLoanQuantity());
        this.loan = loan;
        return showAndWait();
    }
}