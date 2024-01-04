package be.technobel.aquarium.ui;

import be.technobel.aquarium.model.*;
import be.technobel.aquarium.model.poissons_races.*;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AquariumPane extends Pane {
    private static final int FISH_WIDTH = 128;
    private static final int FISH_HEIGHT = 128;
    private final MainWindow mainWindow;
    private final List<AnimationPoisson> animationPoissons = new ArrayList<>();

    public AquariumPane(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /** Retire tous les poissons créés dans l'affichage */
    public void clear() {
        for(AnimationPoisson animationPoisson : animationPoissons) {
            animationPoisson.setAnimated(false);
            getChildren().remove(animationPoisson.getStackPane());
        }
        animationPoissons.clear();
    }

    public void ajouterPoisson(Vivant vivant) {
        ImagePoisson imagePoisson = null;
        if(vivant instanceof Algue) {
            if(vivant.estMort()) imagePoisson = ImagePoisson.ALGUE_MORTE;
            else imagePoisson = ImagePoisson.ALGUE;
        }
        else if(vivant instanceof Poisson<?> && vivant.estMort()) imagePoisson = ImagePoisson.MORT;
        else if(vivant instanceof Bar) imagePoisson = ImagePoisson.BAR;
        else if(vivant instanceof Carpe) imagePoisson = ImagePoisson.CARPE;
        else if(vivant instanceof Merou) imagePoisson = ImagePoisson.MEROU;
        else if(vivant instanceof PoissonClown) imagePoisson = ImagePoisson.CLOWN;
        else if(vivant instanceof Sole) imagePoisson = ImagePoisson.SOLE;
        else if(vivant instanceof Thon) imagePoisson = ImagePoisson.THON;
        assert imagePoisson != null;
        creerPoisson(vivant instanceof Poisson<?> ? ((Poisson<?>)vivant).getNom() : "Algue", imagePoisson, vivant);
    }

    private void creerPoisson(String nom, ImagePoisson imagePoisson, Vivant vivant) {
        // Label pour afficher le nom du poisson
        Label nameLabel = new Label(nom);
        nameLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 );");

        // ImageView pour afficher l'image du poisson
        ImageView imageView = new ImageView(imagePoisson.getImage());
        imageView.setFitWidth(FISH_WIDTH);
        imageView.setFitHeight(FISH_HEIGHT);

        // StackPane pour contenir Label et ImageView, et positionner Label en dessous de ImageView
        StackPane stackPane = new StackPane(imageView, nameLabel);
        nameLabel.translateXProperty().bind(imageView.layoutXProperty().add(FISH_WIDTH/4).subtract(nameLabel.widthProperty().divide(2)));
        nameLabel.translateYProperty().bind(imageView.layoutYProperty().add(FISH_HEIGHT/2));
        getChildren().add(stackPane);

        // Création du poisson animé
        var animationPoisson = new AnimationPoisson(stackPane, vivant, true);
        stackPane.setOnMouseClicked(event -> mainWindow.setLabelStatut(animationPoisson.getLabel()));
        animationPoissons.add(animationPoisson);
    }

    private class AnimationPoisson {
        private final StackPane stackPane;
        private final ImageView imageView;
        private final boolean isSlow;
        private boolean isAnimated;
        private double anciennePositionX;
        private final Vivant vivant;

        AnimationPoisson(StackPane stackPane, Vivant vivant, boolean isAnimated) {
            this.stackPane = stackPane;
            this.imageView = (ImageView) stackPane.getChildren().get(0);
            this.isAnimated = isAnimated;
            this.isSlow = vivant instanceof Algue || vivant.estMort();
            this.anciennePositionX = 0;
            this.vivant = vivant;
            if(isAnimated) animate();
        }

        public String getLabel() {
            if(vivant instanceof Algue algue) {
                return String.format("Algue: %d PV, %d tours %s", algue.getPv(), algue.getAge(),
                    algue.estMort() ? "(mort: "+algue.getCauseMort().getName().toLowerCase()+")": "");
            }
            else if(vivant instanceof Poisson<?> poisson) {
                return String.format("%s (%s): %s, %d PV, %d tours %s", poisson.getNom(),
                    poisson.getRace().getName().toLowerCase(),
                    poisson.getSexe() == Poisson.Sexe.MALE ? "♂" : "♀",
                    poisson.getPv(), poisson.getAge(),
                    poisson.estMort() ? "(mort: "+poisson.getCauseMort().getName().toLowerCase()+")": "");
            }
            return "";
        }

        StackPane getStackPane() {
            return stackPane;
        }

        /** Change la dernière position x enregistrée et change l'orientation du poisson si besoin */
        public void setPosition(double x) {
            boolean movingLeft = this.anciennePositionX > x;
            this.anciennePositionX = x;
            imageView.setScaleX(movingLeft ? -1 : 1);
        }

        /** Définit si le poisson doit être animé */
        public void setAnimated(boolean isAnimated) {
            this.isAnimated = isAnimated;
        }

        private void animate() {
            double newX = ThreadLocalRandom.current().nextDouble() * (getWidth()-FISH_WIDTH);
            double newY = ThreadLocalRandom.current().nextDouble() * (getHeight()-FISH_HEIGHT);
            setPosition(newX);
            double randomDuration = isSlow ? 10 : 5 + ThreadLocalRandom.current().nextDouble(-1, 2);
            TranslateTransition transition = new TranslateTransition(Duration.seconds(randomDuration), getStackPane());
            transition.setToX(newX);
            transition.setToY(newY);
            transition.setOnFinished(event -> {
                if(isAnimated) animate();
            });
            transition.play();
        }
    }
}
