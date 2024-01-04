package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.BookLoan;
import be.technobel.bibliotheque.model.auth.Address;
import be.technobel.bibliotheque.model.auth.UserAccount;
import be.technobel.bibliotheque.ui.helpers.DialogResult;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;

public class FormProfile extends Dialog<DialogResult<UserAccount>> {
    private final TextField login;
    private final PasswordField password;
    private final Label labelError;
    private final TextField lastName;
    private final TextField firstName;
    private final TextField addressCountry;
    private final TextField addressTown;
    private final TextField addressZipCode;
    private final TextField addressStreetName;
    private final TextField addressStreetNumber;
    private final DatePicker birthDate;
    private final Button btnDelete;
    private UserAccount user = null;

    public FormProfile(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Profil");

        // Création des champs du formulaire
        login = new TextField();
        password = new PasswordField();
        labelError = new Label();
        labelError.getStyleClass().add("label-error");
        labelError.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        lastName = new TextField();
        firstName = new TextField();
        addressCountry = new TextField();
        addressTown = new TextField();
        addressZipCode = new TextField();
        addressStreetName = new TextField();
        addressStreetNumber = new TextField();
        birthDate = new DatePicker();

        // Gestion de la validation du formulaire
        btnDelete = new Button("Supprimer le compte");
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button)getDialogPane().lookupButton(ButtonType.OK);
        setResultConverter(buttonType -> null);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            if(lastName.getText().isBlank()) {
                labelError.setText("Le nom de famille ne peut pas être vide.");
                return;
            }
            if(login.getText().isBlank()) {
                labelError.setText("L'identifiant ne peut pas être vide.");
                return;
            }
            try {
                var userQueryResult = DBInstance.get().getUserByLogin(login.getText());
                if(userQueryResult.isPresent() && !userQueryResult.get().getLogin().equals(user.getLogin())) {
                    labelError.setText("L'identifiant existe déjà.");
                    return;
                }
                var newUser = password.getText().isBlank() ? new UserAccount(
                    user.getId(), login.getText(), user.getSecretHash(), user.getSecretSalt(), user.getRole(),
                    lastName.getText(), firstName.getText(), birthDate.getValue(),
                    new Address(
                        user.getAddress().getId(), addressCountry.getText(), addressTown.getText(), addressZipCode.getText(),
                        addressStreetName.getText(), addressStreetNumber.getText()
                    )
                ) : new UserAccount(
                    user.getId(), login.getText(), password.getText(), user.getRole(),
                    lastName.getText(), firstName.getText(), birthDate.getValue(),
                    new Address(
                        user.getAddress().getId(), addressCountry.getText(), addressTown.getText(), addressZipCode.getText(),
                        addressStreetName.getText(), addressStreetNumber.getText()
                    )
                );
                DBInstance.get().update(newUser);
                Message.USER_UPDATE_SUCCESS.showAndWait();
                setResult(new DialogResult<>(DialogResult.Type.UPDATE, newUser));
                close();
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
        btnDelete.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                boolean hasRemainingBooksLoan = DBInstance.get().findBookLoansBy(user, 0, 0)
                    .stream().anyMatch(BookLoan::isPending);
                if(hasRemainingBooksLoan) Message.USER_DELETE_FAILURE.showAndWait();
                else {
                    Message.USER_DELETE.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        try {
                            DBInstance.get().delete(user);
                            Message.USER_DELETE_SUCCESS.showAndWait();
                            setResult(new DialogResult<>(DialogResult.Type.DELETE, user));
                            close();
                        } catch (Database.DatabaseException e) {
                            Message.UNKNOWN_ERROR.showAndWait();
                        }
                    });
                }
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }

        });

        // Création du layout du formulaire
        getDialogPane().setContent(NodeBuilder.scrollV(NodeBuilder.vbox(
            initTitleProfile(),
            initTitleAddress(),
            initTitleCredentials()
        )));
    }

    private TitledPane initTitleProfile() {
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("Prénom"), firstName);
        grid.addRow(1, new Label("Nom de famille"), lastName);
        grid.addRow(2, new Label("Date de naissance"), birthDate);
        return NodeBuilder.titled("Profil", grid);
    }

    private TitledPane initTitleAddress() {
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("Pays"), addressCountry);
        grid.addRow(1, new Label("Ville"), addressTown);
        grid.addRow(2, new Label("Code postal"), addressZipCode);
        grid.addRow(3, new Label("Rue"), addressStreetName);
        grid.addRow(4, new Label("Numéro de rue"), addressStreetNumber);
        return NodeBuilder.titledCollapsible("Adresse", grid);
    }

    private TitledPane initTitleCredentials() {
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("Nom d'utilisateur"), login);
        grid.addRow(1, new Label("Mot de passe"), password);
        grid.addRow(2, labelError);
        grid.addRow(3, btnDelete);
        GridPane.setColumnSpan(labelError, 2);
        GridPane.setColumnSpan(btnDelete, 2);
        return NodeBuilder.titled("Identifiants de connexion", grid);
    }

    public Optional<DialogResult<UserAccount>> display(UserAccount user) {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        var address = user.getAddress();
        addressCountry.setText(address.getCountry());
        addressTown.setText(address.getCity());
        addressZipCode.setText(address.getZipCode());
        addressStreetName.setText(address.getStreetName());
        addressStreetNumber.setText(address.getStreetNumber());
        birthDate.setValue(user.getBirthDate());
        login.setText(user.getLogin());
        password.setText("");
        labelError.setText("");
        btnDelete.setVisible(true);
        this.user = user;
        return showAndWait();
    }
}
