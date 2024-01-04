package be.technobel.bibliotheque.ui;

import be.technobel.bibliotheque.db.DBInstance;
import be.technobel.bibliotheque.db.Database;
import be.technobel.bibliotheque.model.auth.Address;
import be.technobel.bibliotheque.model.auth.Library;
import be.technobel.bibliotheque.ui.helpers.CurrentLibrary;
import be.technobel.bibliotheque.ui.helpers.Message;
import be.technobel.bibliotheque.ui.helpers.NodeBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;

public class FormLibrary extends Dialog<Library>  {
    private final TextField name;
    private final TextField addressCountry;
    private final TextField addressTown;
    private final TextField addressZipCode;
    private final TextField addressStreetName;
    private final TextField addressStreetNumber;
    private final Label labelError;

    public FormLibrary(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Librairie");

        // Création des champs du formulaire
        name = new TextField();
        addressCountry = new TextField();
        addressTown = new TextField();
        addressZipCode = new TextField();
        addressStreetName = new TextField();
        addressStreetNumber = new TextField();
        labelError = new Label();
        labelError.getStyleClass().add("label-error");
        labelError.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        // Création du layout du formulaire
        getDialogPane().setContent(NodeBuilder.vbox(
            initTitleLibrary(),
            initTitleAddress(),
            labelError
        ));

        // Gestion de la validation du formulaire
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        setResultConverter(buttonType -> null);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            if(name.getText().isBlank()) {
                labelError.setText("Le prénom ne peut pas être vide.");
                return;
            }
            if(addressCountry.getText().isBlank()) {
                labelError.setText("Le pays ne peut pas être vide.");
                return;
            }
            if(addressTown.getText().isBlank()) {
                labelError.setText("La ville ne peut pas être vide.");
                return;
            }
            if(addressZipCode.getText().isBlank()) {
                labelError.setText("Le code postal ne peut pas être vide.");
                return;
            }
            if(addressStreetName.getText().isBlank()) {
                labelError.setText("Le nom de rue ne peut pas être vide.");
                return;
            }
            if(addressStreetNumber.getText().isBlank()) {
                labelError.setText("Le numéro de rue ne peut pas être vide.");
                return;
            }
            var library = new Library(
                CurrentLibrary.get().getId(), name.getText(), new Address(
                    CurrentLibrary.get().getAddress().getId(), addressCountry.getText(), addressTown.getText(), addressZipCode.getText(),
                    addressStreetName.getText(), addressStreetNumber.getText()
                )
            );
            try {
                DBInstance.get().update(library);
                CurrentLibrary.set(library);
                Message.LIBRARY_UPDATE_SUCCESS.showAndWait();
                setResult(library);
                close();
            } catch (Database.DatabaseException e) {
                Message.UNKNOWN_ERROR.showAndWait();
            }
        });
    }

    private TitledPane initTitleLibrary() {
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("Nom"), name);
        return NodeBuilder.titled("Librairie", grid);
    }

    private TitledPane initTitleAddress() {
        var grid = NodeBuilder.gridForm();
        grid.addRow(0, new Label("Pays"), addressCountry);
        grid.addRow(1, new Label("Ville"), addressTown);
        grid.addRow(2, new Label("Code postal"), addressZipCode);
        grid.addRow(3, new Label("Rue"), addressStreetName);
        grid.addRow(4, new Label("Numéro de rue"), addressStreetNumber);
        return NodeBuilder.titled("Adresse", grid);
    }

    public Optional<Library> display() {
        var library = CurrentLibrary.get();
        name.setText(library.getName());
        var address = library.getAddress();
        addressCountry.setText(address.getCountry());
        addressTown.setText(address.getCity());
        addressZipCode.setText(address.getZipCode());
        addressStreetName.setText(address.getStreetName());
        addressStreetNumber.setText(address.getStreetNumber());
        labelError.setText("");
        return showAndWait();
    }
}
