package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.poissons_races.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Optional;
import static be.technobel.aquarium.ui.MainWindow.GLOBAL_STYLE;

public class FormulairePoisson extends Dialog<Poisson<?>> {
    private final TextField nomField;
    private final ComboBox<String> sexeComboBox;
    private final Spinner<Integer> ageSpinner;
    private final ComboBox<String> raceComboBox;

    public FormulairePoisson(Stage primaryStage) {
        // Initialisation de la boite de dialogue
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Ajout d'un poisson");
        getDialogPane().setStyle(GLOBAL_STYLE);

        // Création des champs du formulaire
        nomField = new TextField();
        sexeComboBox = new ComboBox<>();
        sexeComboBox.getItems().addAll("Mâle", "Femelle");
        ageSpinner = new Spinner<>(0, 19, 0);
        raceComboBox = new ComboBox<>();
        raceComboBox.getItems().addAll(
            RacePoisson.BAR.getName(),
            RacePoisson.CARPE.getName(),
            RacePoisson.MEROU.getName(),
            RacePoisson.CLOWN.getName(),
            RacePoisson.SOLE.getName(),
            RacePoisson.THON.getName()
        );

        // Création du layout du formulaire
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Nom:"), nomField);
        grid.addRow(1, new Label("Genre:"), sexeComboBox);
        grid.addRow(2, new Label("Âge:"), ageSpinner);
        grid.addRow(3, new Label("Espèce:"), raceComboBox);
        grid.setHgap(10);
        grid.setVgap(10);
        getDialogPane().setContent(grid);

        // Ajouter les boutons OK et Annuler
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convertir les résultats du formulaire en objet Poisson
        setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK) {
                var nom = nomField.getText();
                var sexe = sexeComboBox.getValue().equals("Mâle") ? Poisson.Sexe.MALE : Poisson.Sexe.FEMELLE;
                var age = ageSpinner.getValue();
                var race = RacePoisson.fromName(raceComboBox.getValue());
                return switch(race) {
                    case BAR -> new Bar(nom, age);
                    case CARPE -> new Carpe(nom, sexe, age);
                    case MEROU -> new Merou(nom, age);
                    case CLOWN -> new PoissonClown(nom, sexe, age);
                    case SOLE -> new Sole(nom, sexe, age);
                    case THON -> new Thon(nom, sexe, age);
                };
            }
            return null;
        });
    }

    /**
     * Affiche la boite de dialogue et crée un poisson.
     * @return Le poisson créé
     */
    public Optional<Poisson<?>> afficher() {
        nomField.clear();
        ageSpinner.getValueFactory().setValue(0);
        sexeComboBox.setValue("Mâle");
        raceComboBox.setValue("Bar");
        return showAndWait();
    }
}
