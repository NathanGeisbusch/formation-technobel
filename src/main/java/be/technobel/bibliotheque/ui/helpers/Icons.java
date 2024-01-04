package be.technobel.bibliotheque.ui.helpers;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.io.InputStream;

public enum Icons {
    USER("be/technobel/bibliotheque/user.png");

    private final Image image;

    Icons(String imagePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(imagePath);
        if(inputStream == null) throw new IllegalStateException("Impossible de charger l'image.");
        image = new Image(inputStream);
    }

    public Image getImage() {
        return image;
    }

    public Image getImage(int width, int height) {
        WritableImage resizedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = resizedImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sourceX = x * image.getWidth() / width;
                double sourceY = y * image.getHeight() / height;
                pixelWriter.setArgb(x, y, image.getPixelReader().getArgb((int) sourceX, (int) sourceY));
            }
        }
        return resizedImage;
    }
}
