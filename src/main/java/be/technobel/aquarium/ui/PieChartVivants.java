package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.Algue;
import be.technobel.aquarium.model.Aquarium;
import be.technobel.aquarium.model.poissons_races.*;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class PieChartVivants extends BorderPane {
    private final PieChart pieChart = new PieChart();
    private final Aquarium aquarium;

    public PieChartVivants(Aquarium aquarium) {
        this.aquarium = aquarium;
        setPadding(new Insets(50, 50, 50, 50));
        setCenter(pieChart);
    }

    public void refresh() {
        // Récupération des données
        pieChart.getData().clear();
        PieChart.Data algues = new PieChart.Data("Algues",
            aquarium.getVivants().filter(v -> v instanceof Algue).count()
        );
        PieChart.Data bars = new PieChart.Data("Bars",
            aquarium.getVivants().filter(v -> v instanceof Bar).count()
        );
        PieChart.Data carpes = new PieChart.Data("Carpes",
            aquarium.getVivants().filter(v -> v instanceof Carpe).count()
        );
        PieChart.Data merous = new PieChart.Data("Mérous",
            aquarium.getVivants().filter(v -> v instanceof Merou).count()
        );
        PieChart.Data poissonsClown = new PieChart.Data("Poissons-Clown",
            aquarium.getVivants().filter(v -> v instanceof PoissonClown).count()
        );
        PieChart.Data soles = new PieChart.Data("Soles",
            aquarium.getVivants().filter(v -> v instanceof Sole).count()
        );
        PieChart.Data thons = new PieChart.Data("Thons",
            aquarium.getVivants().filter(v -> v instanceof Thon).count()
        );
        pieChart.getData().addAll(algues, bars, carpes, merous, poissonsClown, soles, thons);

        // Modification de l'affichage pour afficher les pourcentages et la quantité
        long total = aquarium.getVivants().count();
        pieChart.getData().forEach(data -> data.nameProperty().bindBidirectional(data.pieValueProperty(), new StringConverter<>() {
            @Override
            public String toString(Number object) {
                var name = data.getName();
                if(total == 0) return String.format("%s %.2f%% (%d)", name, 0.0, 0);
                double percentage = (object.doubleValue() / total) * 100;
                return String.format("%s %.2f%% (%d)", name, percentage, object.longValue());
            }
            // Pas nécessaire pour l'affichage
            @Override public Number fromString(String string) {
                return null;
            }
        }));
        // Force le rafraichissement de l'affichage (permet de corriger un bug d'affichage du nom des données)
        pieChart.layout();
    }
}
