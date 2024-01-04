package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.Algue;
import be.technobel.aquarium.model.Aquarium;
import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.Statistiques;
import be.technobel.aquarium.model.poissons_races.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainWindow extends Application {
    public static final String GLOBAL_STYLE = "-fx-font-size: 16px;";
    private final BorderPane rootPane;
    private final BorderPane mainPane;
    private final AquariumPane aquariumPane;
    private final ListView<String> logListView;
    private final ObservableList<String> logs;
    private final Label labelStatut;
    private final Slider sliderTour = new Slider(0, 0, 0);
    private final Label labelTour = new Label();
    private FormulairePoisson dialogPoisson;
    private FormulaireAlgue dialogAlgue;

    private final Aquarium aquarium = new Aquarium();
    private final Statistiques stats = aquarium.getStatistiques();
    private final PieChartVivants pieChartVivants = new PieChartVivants(aquarium);
    private final PieChartMorts pieChartMorts = new PieChartMorts(stats);
    private final LineChartMorts lineChartMorts = new LineChartMorts(stats);
    private final LineChartNaissances lineChartNaissances = new LineChartNaissances(stats);

    public MainWindow() {
        rootPane = new BorderPane();
        mainPane = new BorderPane();
        rootPane.setStyle(GLOBAL_STYLE);
        setupTabs();
        setupToolBar();
        labelStatut = setupStatusBar();
        logListView = setupLogPanel();
        logs = logListView.getItems();
        aquariumPane = setupAquariumPane();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aquarium");
        primaryStage.setMaximized(true);
        primaryStage.setOnShown((WindowEvent event) -> {
            this.dialogPoisson = new FormulairePoisson(primaryStage);
            this.dialogAlgue = new FormulaireAlgue(primaryStage);
            logListView.setPrefWidth(720);
            initAquarium();
            aquarium.getVivants().forEach(aquariumPane::ajouterPoisson);
            pieChartVivants.refresh();
            pieChartMorts.refresh();
            lineChartMorts.refresh();
            lineChartNaissances.refresh();
        });
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** Initialise le contenu de l'aquarium */
    private void initAquarium() {
        aquarium.ajouterAlgue(new Algue());
        aquarium.ajouterPoisson(
            new Carpe("Métacarpe", Poisson.Sexe.FEMELLE),
            new Carpe("Magicarpe", Poisson.Sexe.MALE),
            new Merou("Devon", 2),
            new Merou("Roxanne", 10),
            new Merou("Yves", 5),
            new Thon("Bary", Poisson.Sexe.MALE),
            new Thon("Mara", Poisson.Sexe.FEMELLE),
            new Bar("Raymond", 5),
            new Bar("Escobar", 5),
            new Bar("Malabar"),
            new Bar("Loubar"),
            new Sole("Klakeo", Poisson.Sexe.MALE, 19),
            new PoissonClown("Corail", Poisson.Sexe.FEMELLE, 15),
            new PoissonClown("Marin", Poisson.Sexe.MALE, 15),
            new PoissonClown("Nemo", Poisson.Sexe.MALE, 5)
        );
    }

    /** Initialise les onglets (aquarium, statistiques, ...) */
    private void setupTabs() {
        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab("Aquarium");
        Tab tab2 = new Tab("Répartition des poissons");
        Tab tab3 = new Tab("Répartition des morts");
        Tab tab4 = new Tab("Évolution des décès");
        Tab tab5 = new Tab("Évolution des naissances");
        tab1.setClosable(false);
        tab2.setClosable(false);
        tab3.setClosable(false);
        tab4.setClosable(false);
        tab5.setClosable(false);
        tab1.setContent(mainPane);
        tab2.setContent(pieChartVivants);
        tab3.setContent(pieChartMorts);
        tab4.setContent(lineChartMorts);
        tab5.setContent(lineChartNaissances);
        tabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5);
        rootPane.setCenter(tabPane);
    }

    /** Initialise la barre d'outils */
    private void setupToolBar() {
        // Boutons de la barre d'outils
        ToolBar toolBar = new ToolBar();
        Button newTurnButton = new Button("Nouveau tour");
        Button addFishButton = new Button("Ajouter poisson");
        Button addAlgaButton = new Button("Ajouter algue");
        Button cleanButton = new Button("Nettoyer");

        // Actions à exécuter lors de click sur les boutons
        cleanButton.setOnAction(e -> nettoyerAquarium());
        newTurnButton.setOnAction(e -> prochainTour());
        addFishButton.setOnAction(e -> {
            var result = dialogPoisson.afficher();
            result.ifPresent(poisson -> {
                aquarium.ajouterPoisson(poisson);
                rechargerDernierTour();
            });
        });
        addAlgaButton.setOnAction(e -> {
            var result = dialogAlgue.afficher();
            result.ifPresent(algue -> {
                aquarium.ajouterAlgue(algue);
                rechargerDernierTour();
            });
        });

        // Paramétrage du slider gérant la sélection du tour à afficher
        Separator separator = new Separator(); // Séparateur entre les premiers boutons et le slider
        // Définit que le changement de valeur doit se faire par étape de 1 (car slider utilise des décimales)
        sliderTour.setMajorTickUnit(1);
        // Action à exécuter lors d'un changement de la valeur du slider
        sliderTour.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(sliderTour.getMax() < 1F) return; // Si aucun tour créé, ne rien faire
            int index = newValue.intValue();
            // Affiche le numéro de tour actuel sur le nombre total de tours
            labelTour.setText(String.format("Tour: %d/%d", index, (int) sliderTour.getMax()));
            // Affiche le tour sélectionné
            changerTour(index);
        });

        // Ajout des éléments à la barre d'outils
        toolBar.getItems().addAll(newTurnButton, addFishButton, addAlgaButton, cleanButton, separator, sliderTour, labelTour);
        // Ajout de la barre d'outils en haut de la fenêtre
        mainPane.setTop(toolBar);
    }

    /**
     * Initialise l'affichage de l'aquarium au centre de la fenêtre
     * @return {@link AquariumPane L'affichage de l'aquarium}
     */
    private AquariumPane setupAquariumPane() {
        var aquariumPane = new AquariumPane(this);
        aquariumPane.setStyle("-fx-background-color: #206080;");
        mainPane.setCenter(aquariumPane);
        return aquariumPane;
    }

    /**
     * Initialise la liste de logs à droite de la fenêtre
     * @return La liste de logs
     */
    private ListView<String> setupLogPanel() {
        var logListView = new ListView<String>();
        mainPane.setRight(logListView);
        return logListView;
    }

    /**
     * Initialise la barre de statut en bas de la fenêtre
     * @return Le label de cette barre de statut
     */
    private Label setupStatusBar() {
        Label labelStatut = new Label("");
        HBox statusBar = new HBox(labelStatut);
        statusBar.setStyle("-fx-background-color: #E0E0E0;");
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        mainPane.setBottom(statusBar);
        return labelStatut;
    }

    /** Modifie le texte de la barre de statut en bas de la fenêtre */
    public void setLabelStatut(String text) {
        this.labelStatut.setText(text);
    }

    /** Génère le prochain tour et l'affiche */
    private void prochainTour() {
        aquarium.prochainTour();
        sliderTour.setMax(stats.nbTours()-1);
        sliderTour.setValue(stats.nbTours()-1);
        aquariumPane.clear();
        stats.getTourActuel().getVivants().forEach(aquariumPane::ajouterPoisson);
        logs.clear();
        stats.getTourActuel().getLogs().forEach(l -> logs.add(l.toString()));
        pieChartVivants.refresh();
        pieChartMorts.refresh();
        lineChartMorts.refresh();
        lineChartNaissances.refresh();
    }

    /** Affiche le tour spécifié en paramètre */
    private void changerTour(int index) {
        if(index >= stats.nbTours()) throw new IllegalArgumentException();
        var tour = stats.getTour(index);
        aquariumPane.clear();
        logs.clear();
        tour.getVivants().forEach(aquariumPane::ajouterPoisson);
        tour.getLogs().forEach(l -> logs.add(l.toString()));
    }

    /** Retire tous les poissons et algues morts. */
    private void nettoyerAquarium() {
        aquarium.nettoyer();
        this.rechargerDernierTour();
    }

    /** Ré-affiche le dernier tour avec les dernières modifications faites à l'aquarium */
    private void rechargerDernierTour() {
        if(stats.nbTours() > 0) {
            sliderTour.setValue(stats.nbTours()-1);
            logs.clear();
            stats.getTourActuel().getLogs().forEach(l -> logs.add(l.toString()));
        }
        aquariumPane.clear();
        aquarium.getVivants().forEach(aquariumPane::ajouterPoisson);
    }

    public static void run() {
        launch();
    }
}
