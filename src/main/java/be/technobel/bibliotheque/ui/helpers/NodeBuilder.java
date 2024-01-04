package be.technobel.bibliotheque.ui.helpers;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;

public class NodeBuilder {
    public static GridPane gridForm() {
        // Contraintes
        ColumnConstraints neverGrow = new ColumnConstraints();
        ColumnConstraints alwaysGrow = new ColumnConstraints();
        neverGrow.setHgrow(Priority.NEVER);
        alwaysGrow.setHgrow(Priority.ALWAYS);
        // Grid
        GridPane grid = new GridPane();
        grid.getColumnConstraints().addAll(alwaysGrow, neverGrow);
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    public static GridPane gridFormGrow() {
        ColumnConstraints alwaysGrow = new ColumnConstraints();
        //alwaysGrow.setHgrow(Priority.ALWAYS);
        alwaysGrow.setPercentWidth(100);
        GridPane grid = new GridPane();
        grid.getColumnConstraints().addAll(alwaysGrow, alwaysGrow);
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    public static TitledPane titled(String title, GridPane grid) {
        TitledPane titled = new TitledPane();
        titled.setText(title);
        titled.setContent(grid);
        titled.setCollapsible(false);
        return titled;
    }

    public static TitledPane titledCollapsible(String title, GridPane grid) {
        TitledPane titled = new TitledPane();
        titled.setText(title);
        titled.setContent(grid);
        titled.setExpanded(false);
        return titled;
    }

    public static VBox vbox(Node... nodes) {
        VBox box = new VBox();
        box.setSpacing(10);
        box.getChildren().addAll(nodes);
        return box;
    }

    public static HBox hbox(Node... nodes) {
        HBox box = new HBox();
        box.setSpacing(10);
        box.getChildren().addAll(nodes);
        return box;
    }

    public static ScrollPane scrollV(Node content) {
        ScrollPane sp = new ScrollPane();
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.setContent(content);
        return sp;
    }
}
