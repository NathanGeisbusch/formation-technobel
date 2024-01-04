package be.technobel.bibliotheque.ui.helpers;

import be.technobel.bibliotheque.model.auth.Library;

public final class CurrentLibrary {
    private static Library library;
    private CurrentLibrary() {}
    public static void set(Library library) {
        CurrentLibrary.library = library;
    }
    public static Library get() {
        return library;
    }
}
