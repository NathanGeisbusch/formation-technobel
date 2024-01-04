package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.auth.UserAccount;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;

public class LoansWindow extends Dialog<Object> {
    private TableView<BookLoan> tableLoans;
    private final FormLoanUpdate formLoanUpdate;
    private UserAccount user;

    public LoansWindow(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Emprunts");
        formLoanUpdate = new FormLoanUpdate(primaryStage);
        initTable();
        // Création du layout
        var sp = NodeBuilder.scrollV(tableLoans);
        sp.setStyle("-fx-padding: 0;");
        getDialogPane().setContent(sp);
        // Gestion de la validation du formulaire
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
    }

    public void initTable() {
        tableLoans = new TableView<>();
        tableLoans.setStyle("-fx-padding: 0;");
        tableLoans.setPrefWidth(1000);
        tableLoans.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<BookLoan, String> colLoanDate = new TableColumn<>("Date d'emprunt");
        colLoanDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoanDate().toString()));
        TableColumn<BookLoan, String> colLoanQty = new TableColumn<>("Quantité empruntée");
        colLoanQty.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLoanQuantity()).asString());
        TableColumn<BookLoan, String> colStatus = new TableColumn<>("Statut");
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isPending() ? "À rendre" : "Rendu"));
        TableColumn<BookLoan, String> colTitle = new TableColumn<>("Titre");
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        tableLoans.getColumns().addAll(colLoanDate, colLoanQty, colStatus, colTitle);
        initTableContextMenu();

        VBox pane = new VBox();
        pane.getChildren().addAll(tableLoans);
        VBox.setVgrow(tableLoans, javafx.scene.layout.Priority.ALWAYS);
    }

    public void initTableContextMenu() {
        tableLoans.setRowFactory(tableView -> {
            // Context menu
            final TableRow<BookLoan> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();
            rowMenu.getStyleClass().add("table-context-menu");
            ContextMenu tableMenu = tableView.getContextMenu();
            if(tableMenu != null) {
                rowMenu.getItems().addAll(tableMenu.getItems());
                rowMenu.getItems().add(new SeparatorMenuItem());
            }
            MenuItem itemReturn = new MenuItem("Rendre");
            MenuItem itemDelete = new MenuItem("Supprimer");
            row.contextMenuProperty().bind(
                Bindings.when(Bindings.isNotNull(row.itemProperty()))
                .then(rowMenu)
                .otherwise((ContextMenu) null)
            );
            // Actions
            itemReturn.setOnAction(e -> formLoanUpdate.display(row.getItem()).ifPresent(result -> refreshTableData()));
            itemDelete.setOnAction(e -> Message.LOAN_DELETE.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK) {
                    var loan = row.getItem();
                    if(loan.isPending()) Message.LOAN_DELETE_FAILURE.showAndWait();
                    else {
                        try {
                            DBInstance.get().delete(loan);
                            Message.LOAN_DELETE_SUCCESS.showAndWait();
                            refreshTableData();
                        } catch (Database.DatabaseException ex) {
                            Message.UNKNOWN_ERROR.showAndWait();
                        }
                    }
                }
            }));
            rowMenu.getItems().addAll(itemReturn, itemDelete);
            return row;
        });
    }

    public void refreshTableData() {
        tableLoans.getItems().clear();
        try {
            tableLoans.getItems().addAll(
                DBInstance.get().findBookLoansBy(user,0, 0)
            );
        } catch (Database.DatabaseException e) {
            Message.UNKNOWN_ERROR.showAndWait();
        }
    }

    public Optional<Object> display(UserAccount user) {
        this.user = user;
        refreshTableData();
        return showAndWait();
    }
}
