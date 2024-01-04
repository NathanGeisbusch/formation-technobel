package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.db.mock.Mock;
import be.technobel.bibliotheque.db.sql.SQLiteDB;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.BookStock;
import be.technobel.bibliotheque.model.auth.*;
import be.technobel.bibliotheque.ui.helpers.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow extends Application {
    private final BorderPane rootPane;
    private TableView<BookStock> tableBook;
    private FormConnect formConnect;
    private FormSubscription formSubscription;
    private FormProfile formProfile;
    private FormLibrary formLibrary;
    private FormBookAdd formBookAdd;
    private FormBookUpdate formBookUpdate;
    private FormLoanAdd formLoanAdd;
    private LoansWindow loansWindow;
    private UserSession userSession;

    public static void run() {
        try {
            //DBInstance.init(new MockDB());
            DBInstance.init(new SQLiteDB());
            if(DBInstance.get().getLibrary(1).isEmpty()) Mock.populateDatabase(DBInstance.get());
            CurrentLibrary.set(DBInstance.get().getLibrary(1).orElseGet(() -> {
                var newLibrary = new Library("", new Address());
                try {
                    DBInstance.get().create(newLibrary);
                } catch (Database.DatabaseException e) {
                    Message.UNKNOWN_ERROR.showAndWait();
                }
                return newLibrary;
            }));
        } catch (Database.DatabaseException e) {
            throw new RuntimeException();
        }
        launch();
    }

    public MainWindow() {
        rootPane = new BorderPane();
        resetUI();
        try {
            connect(DBInstance.get().getUser(1).orElseThrow());
        } catch (Database.DatabaseException e) {
            Message.UNKNOWN_ERROR.showAndWait();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bibliothèque");
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(event -> {
            try {
                DBInstance.close();
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
        primaryStage.setOnShown(event -> {
            formConnect = new FormConnect(primaryStage);
            formSubscription = new FormSubscription(primaryStage);
            formProfile = new FormProfile(primaryStage);
            formLibrary = new FormLibrary(primaryStage);
            formBookAdd = new FormBookAdd(primaryStage);
            formBookUpdate = new FormBookUpdate(primaryStage);
            formLoanAdd = new FormLoanAdd(primaryStage);
            loansWindow = new LoansWindow(primaryStage);
        });
        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add(StyleLoader.GLOBAL.getPath());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void resetUI() {
        rootPane.getChildren().clear();
        initNavbar();
        initTable();
        refreshTableData();
    }

    public void initNavbar() {
        MenuBar navbar = new MenuBar();

        // Menu User
        Menu menuUser = new Menu("Non connecté");
        MenuItem menuSubscribe = new MenuItem("S'inscrire");
        MenuItem menuConnect = new MenuItem("Se connecter");
        MenuItem menuDisconnect = new MenuItem("Se déconnecter");
        MenuItem menuUpdateProfile = new MenuItem("Modifier profil");
        MenuItem menuUpdateLibrary = new MenuItem("Modifier bibliothèque");
        MenuItem menuLoans = new MenuItem("Mes emprunts");
        ImageView userIcon = new ImageView(Icons.USER.getImage(20, 20));
        userIcon.setFitHeight(20);
        userIcon.setFitWidth(20);
        userIcon.setPreserveRatio(true);
        userIcon.setSmooth(true);
        menuUser.setGraphic(userIcon);

        // Menu Books
        Menu menuBooks = new Menu("Livres");
        MenuItem menuAddBook = new MenuItem("Ajouter");
        menuBooks.getItems().addAll(menuAddBook);

        // Actions
        menuConnect.setOnAction(e -> formConnect.display().ifPresent(this::connect));
        menuDisconnect.setOnAction(e -> disconnect());
        menuSubscribe.setOnAction(e -> formSubscription.display().ifPresent(user -> {}));
        menuUpdateProfile.setOnAction(e -> formProfile.display(userSession.getUser()).ifPresent(result -> {
            var user = result.value();
            var type = result.type();
            if(type == DialogResult.Type.UPDATE) connect(user);
            else if(type == DialogResult.Type.DELETE) disconnect();
        }));
        menuUpdateLibrary.setOnAction(e -> formLibrary.display().ifPresent(library -> {}));
        menuLoans.setOnAction(e -> {
            loansWindow.display(userSession.getUser());
            refreshTableData();
        });
        menuAddBook.setOnAction(e -> formBookAdd.display().ifPresent(result -> refreshTableData()));

        // Permissions
        if(userSession != null) {
            var user = userSession.getUser();
            menuUser.setText(user.getLogin());
            if(user.getRole() == UserRole.USER) {
                menuUser.getItems().addAll(menuSubscribe, menuDisconnect, menuUpdateProfile, menuLoans);
                navbar.getMenus().addAll(menuUser);
            }
            else if(user.getRole() == UserRole.ADMIN) {
                menuUser.getItems().addAll(menuSubscribe, menuDisconnect, menuUpdateProfile, menuUpdateLibrary, menuLoans);
                navbar.getMenus().addAll(menuUser, menuBooks);
            }
        }
        else {
            menuUser.getItems().addAll(menuSubscribe, menuConnect);
            navbar.getMenus().addAll(menuUser);
        }
        rootPane.setTop(navbar);
    }

    public void initTable() {
        tableBook = new TableView<>();
        tableBook.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<BookStock, String> colISBN = new TableColumn<>("ISBN");
        colISBN.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBook().getISBN()));
        TableColumn<BookStock, String> colTitle = new TableColumn<>("Titre");
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        TableColumn<BookStock, String> colAuthor = new TableColumn<>("Auteur");
        colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBook().getAuthor()));
        TableColumn<BookStock, String> colDate = new TableColumn<>("Date de publication");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBook().getPublicationDate().toString()));
        TableColumn<BookStock, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getQuantity()).asString());
        tableBook.getColumns().addAll(colISBN, colTitle, colAuthor, colDate, colStock);
        initTableContextMenu();

        /*Pagination pagination = new Pagination(1);
        HBox subTable = new HBox();
        subTable.setAlignment(Pos.CENTER);
        subTable.setSpacing(10);
        subTable.getChildren().addAll(pagination);*/

        VBox pane = new VBox();
        pane.getChildren().addAll(tableBook); //, subTable);
        VBox.setVgrow(tableBook, javafx.scene.layout.Priority.ALWAYS);
        rootPane.setCenter(pane);
    }

    public void initTableContextMenu() {
        tableBook.setRowFactory(tableView -> {
            // Context menu
            final TableRow<BookStock> row = new TableRow<>();
            if(userSession == null) return row;
            final ContextMenu rowMenu = new ContextMenu();
            rowMenu.getStyleClass().add("table-context-menu");
            ContextMenu tableMenu = tableView.getContextMenu();
            if(tableMenu != null) {
                rowMenu.getItems().addAll(tableMenu.getItems());
                rowMenu.getItems().add(new SeparatorMenuItem());
            }
            MenuItem itemLoan = new MenuItem("Emprunter");
            MenuItem itemEdit = new MenuItem("Modifier");
            MenuItem itemDelete = new MenuItem("Supprimer");
            row.contextMenuProperty().bind(
                Bindings.when(Bindings.isNotNull(row.itemProperty()))
                .then(rowMenu)
                .otherwise((ContextMenu) null)
            );
            // Actions
            itemLoan.setOnAction(e -> {
                var stock = row.getItem();
                if(stock.getQuantity() == 0) Message.LOAN_OUT_OF_STOCK.showAndWait();
                else formLoanAdd.display(userSession.getUser(), stock).ifPresent(result -> refreshTableData());
            });
            itemEdit.setOnAction(e -> formBookUpdate.display(row.getItem()).ifPresent(result -> refreshTableData()));
            itemDelete.setOnAction(e -> Message.BOOK_DELETE.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK) {
                    var stock = row.getItem();
                    var book = stock.getBook();
                    try {
                        var havePendingLoans = DBInstance.get().findBookLoansBy(book,0,0)
                            .stream().anyMatch(BookLoan::isPending);
                        if(havePendingLoans) Message.BOOK_DELETE_FAILURE.showAndWait();
                        else {
                            DBInstance.get().deleteBookLoanBy(book);
                            DBInstance.get().deleteBookStockBy(book);
                            DBInstance.get().delete(book);
                            Message.BOOK_DELETE_SUCCESS.showAndWait();
                            refreshTableData();
                        }
                    } catch (Database.DatabaseException ex) {
                        Message.UNKNOWN_ERROR.showAndWait();
                    }
                }
            }));
            // Permissions
            var user = userSession.getUser();
            if(user.getRole() == UserRole.USER) rowMenu.getItems().addAll(itemLoan);
            else if(user.getRole() == UserRole.ADMIN) rowMenu.getItems().addAll(itemLoan, itemEdit, itemDelete);
            return row;
        });
    }

    public void refreshTableData() {
        tableBook.getItems().clear();
        try {
            tableBook.getItems().addAll(
                DBInstance.get().findBookStocksBy(CurrentLibrary.get(),0, 0)
            );
        } catch (Database.DatabaseException e) {
            Message.UNKNOWN_ERROR.showAndWait();
        }
    }

    public void disconnect() {
        userSession = null;
        resetUI();
    }

    public void connect(UserAccount user) {
        userSession = new UserSession(user);
        resetUI();
    }
}
