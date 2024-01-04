package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.auth.Address;
import be.technobel.bibliotheque.model.auth.UserAccount;
import be.technobel.bibliotheque.model.auth.UserRole;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Optional;

public class FormSubscription extends Dialog<UserAccount> {
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

    public FormSubscription(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Inscription");

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
            if(password.getText().isBlank()) {
                labelError.setText("Le mot de passe ne peut pas être vide.");
                return;
            }

            try {
                var userQueryResult = DBInstance.get().getUserByLogin(login.getText());
                if(userQueryResult.isPresent()) {
                    labelError.setText("L'identifiant existe déjà.");
                    return;
                }
                var newUser = new UserAccount(
                    login.getText(), password.getText(), UserRole.USER,
                    lastName.getText(), firstName.getText(), birthDate.getValue(),
                    new Address(
                        addressCountry.getText(), addressTown.getText(), addressZipCode.getText(),
                        addressStreetName.getText(), addressStreetNumber.getText()
                    )
                );
                DBInstance.get().create(newUser);
                Message.USER_ADD_SUCCESS.showAndWait();
                setResult(newUser);
                close();
            } catch (Database.AddressNoRemainingIdException | Database.UserNoRemainingIdException e) {
                Message.DB_FULL.showAndWait();
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
        GridPane.setColumnSpan(labelError, 2);
        return NodeBuilder.titled("Identifiants de connexion", grid);
    }

    public Optional<UserAccount> display() {
        firstName.clear();
        lastName.clear();
        addressCountry.clear();
        addressTown.clear();
        addressZipCode.clear();
        addressStreetName.clear();
        addressStreetNumber.clear();
        birthDate.setValue(LocalDate.now());
        login.clear();
        password.clear();
        labelError.setText("");
        return showAndWait();
    }
}
