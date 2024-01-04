package be.technobel.bibliotheque.ui.helpers;

public record DialogResult<T>(DialogResult.Type type, T value) {
    public enum Type {GET,ADD,UPDATE,DELETE};
}
