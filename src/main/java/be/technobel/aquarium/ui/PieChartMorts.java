package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.Statistiques;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class PieChartMorts extends BorderPane {
    private final PieChart pieChart = new PieChart();
    private final Statistiques stats;

    public PieChartMorts(Statistiques stats) {
        this.stats = stats;
        setPadding(new Insets(50, 50, 50, 50));
        setCenter(pieChart);
    }

    public void refresh() {
        // Récupération des données
        pieChart.getData().clear();
        var causesMorts = stats.getCausesMorts();
        causesMorts.forEach(causeMortStat -> {
            var data = new PieChart.Data(causeMortStat.key().getName(), causeMortStat.value());
            pieChart.getData().add(data);
        });

        // Modification de l'affichage pour afficher les pourcentages et la quantité
        long total = causesMorts.stream().map(Statistiques.Stat::value).reduce(Long::sum).orElse(0L);
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