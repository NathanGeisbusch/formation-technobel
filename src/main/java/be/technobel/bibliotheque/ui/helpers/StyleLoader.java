package be.technobel.bibliotheque.ui.helpers;

public enum StyleLoader {
    GLOBAL("be/technobel/bibliotheque/app.css");

    private final String path;

    StyleLoader(String stylePath) {
        var url = Thread.currentThread().getContextClassLoader().getResource(stylePath);
        if(url == null) throw new IllegalStateException("Impossible de charger le style.");
        path = url.toString();
    }

    public String getPath() {
        return path;
    }
}
