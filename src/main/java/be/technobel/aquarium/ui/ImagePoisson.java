package be.technobel.aquarium.ui;

import javafx.scene.image.Image;
import java.io.InputStream;

public enum ImagePoisson {
    ALGUE("assets/img/algue.png"),
    ALGUE_MORTE("assets/img/algue_morte.png"),
    MORT("assets/img/mort-blanc.png"),
    BAR("assets/img/bar.png"),
    CARPE("assets/img/carpe.png"),
    CLOWN("assets/img/clown.png"),
    MEROU("assets/img/merou.png"),
    SOLE("assets/img/sole.png"),
    THON("assets/img/thon.png");

    private final Image image;

    ImagePoisson(String imagePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(imagePath);
        if(inputStream == null) throw new IllegalStateException("Impossible de charger l'image.");
        image = new Image(inputStream);
    }

    public Image getImage() {
        return image;
    }
}
