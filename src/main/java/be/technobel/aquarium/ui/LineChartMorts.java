package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.Statistiques;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

public class LineChartMorts extends BorderPane {
    private final XYChart.Series<Number, Number> alguesSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> poissonsSeries = new XYChart.Series<>();
    private final Statistiques stats;

    public LineChartMorts(Statistiques stats) {
        this.stats = stats;
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Nombre de tours");
        yAxis.setLabel("Nombre de morts");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Nombre de morts en fonction du nombre de tours");
        lineChart.setLegendVisible(false);
        alguesSeries.setName("Nombre d'algues");
        poissonsSeries.setName("Nombre de poissons");
        lineChart.getData().addAll(alguesSeries, poissonsSeries);
        setCenter(lineChart);
        setPadding(new Insets(50, 50, 50, 50));
    }

    public void refresh() {
        // Récupération des données
        var dataAlgues = alguesSeries.getData();
        var dataPoissons = poissonsSeries.getData();
        dataAlgues.clear();
        dataPoissons.clear();
        stats.getAlguesMortes().forEach(p -> dataAlgues.add(new XYChart.Data<>(p.key(), p.value())));
        stats.getPoissonsMorts().forEach(p -> dataPoissons.add(new XYChart.Data<>(p.key(), p.value())));

        // Modification de l'affichage pour afficher la ligne pour les poissons en bleu, et en vert pour les algues
        if(alguesSeries.getNode() != null) {
            alguesSeries.getNode().setStyle("-fx-stroke: green;");
            poissonsSeries.getNode().setStyle("-fx-stroke: blue;");
            for(XYChart.Data<Number, Number> data : alguesSeries.getData()) {
                data.nodeProperty().get().setStyle("-fx-background-color: green, white;");
            }
            for(XYChart.Data<Number, Number> data : poissonsSeries.getData()) {
                data.nodeProperty().get().setStyle("-fx-background-color: blue, white;");
            }
        }
    }
}
