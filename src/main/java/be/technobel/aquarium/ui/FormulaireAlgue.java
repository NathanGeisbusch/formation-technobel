package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.Algue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;
import static be.technobel.aquarium.ui.MainWindow.GLOBAL_STYLE;

public class FormulaireAlgue extends Dialog<Algue> {
    private final Spinner<Integer> ageSpinner;

    public FormulaireAlgue(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Ajout d'une algue");
        getDialogPane().setStyle(GLOBAL_STYLE);

        // Création des champs du formulaire
        ageSpinner = new Spinner<>(0, 19, 0);

        // Création du layout du formulaire
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Âge:"), ageSpinner);
        grid.setHgap(10);
        grid.setVgap(10);
        getDialogPane().setContent(grid);

        // Ajouter les boutons OK et Annuler
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convertir les résultats du formulaire en objet Algue
        setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK) {
                var age = ageSpinner.getValue();
                return new Algue(age);
            }
            return null;
        });
    }

    /**
     * Affiche la boite de dialogue et crée une algue.
     * @return L'algue créée
     */
    public Optional<Algue> afficher() {
        ageSpinner.getValueFactory().setValue(0);
        return showAndWait();
    }
}
