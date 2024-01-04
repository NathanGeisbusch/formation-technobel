package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.auth.UserAccount;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;

public class FormConnect extends Dialog<UserAccount> {
    private final TextField login;
    private final PasswordField password;
    private final Label labelError;

    public FormConnect(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Connexion");

        // Création des champs du formulaire
        login = new TextField();
        password = new PasswordField();
        labelError = new Label();
        labelError.getStyleClass().add("label-error");
        labelError.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        // Création du layout du formulaire
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("Identifiant"), login);
        grid.addRow(1, new Label("Mot de passe"), password);
        grid.addRow(2, labelError);
        GridPane.setColumnSpan(labelError, 2);
        getDialogPane().setContent(grid);

        // Gestion de la validation du formulaire
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button)getDialogPane().lookupButton(ButtonType.OK);
        setResultConverter(buttonType -> null);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
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
                if(userQueryResult.isEmpty()) {
                    // Pour plus de confidentialité : "Identifiant ou mot de passe invalide"
                    labelError.setText("Cet identifiant n'existe pas.");
                    return;
                }
                var user = userQueryResult.get();
                if(!user.verifyPassword(password.getText())) {
                    // Pour plus de confidentialité : "Identifiant ou mot de passe invalide"
                    labelError.setText("Mot de passe invalide.");
                    return;
                }
                setResult(user);
                close();
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
    }

    public Optional<UserAccount> display() {
        login.clear();
        password.clear();
        labelError.setText("");
        return showAndWait();
    }
}
