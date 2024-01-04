package be.technobel.bibliotheque.ui.helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public enum Message {
    USER_DELETE(
        "Suppression de l'utilisateur", "Êtes-vous certain de vouloir supprimer votre compte ?",
        Alert.AlertType.WARNING, ButtonType.OK, ButtonType.CANCEL
    ),
    USER_DELETE_SUCCESS(
        "Confirmation de suppression",
        "Votre compte a été supprimé avec succès !",
        Alert.AlertType.INFORMATION
    ),
    USER_DELETE_FAILURE(
        "Suppression impossible",
        "Vous devez rapporter vos livres empruntés avant de pouvoir supprimer votre compte !",
        Alert.AlertType.WARNING
    ),
    USER_UPDATE_SUCCESS(
        "Confirmation de modification",
        "Informations modifiées avec succès !",
        Alert.AlertType.INFORMATION
    ),
    USER_ADD_SUCCESS(
        "Confirmation d'inscription",
        "Inscription effectuée avec succès !",
        Alert.AlertType.INFORMATION
    ),
    BOOK_ALREADY_EXISTS(
        "Ajout impossible",
        "Le livre existe déjà dans la base de données !",
        Alert.AlertType.WARNING
    ),
    BOOK_ADD_SUCCESS(
        "Confirmation d'ajout",
        "Le livre a été ajouté avec succès !",
        Alert.AlertType.INFORMATION
    ),
    BOOK_UPDATE_SUCCESS(
        "Confirmation de modification",
        "Le livre a été modifié avec succès !",
        Alert.AlertType.INFORMATION
    ),
    BOOK_DELETE(
        "Suppression d'un livre", "Êtes-vous certain de vouloir supprimer ce livre ?",
        Alert.AlertType.WARNING, ButtonType.OK, ButtonType.CANCEL
    ),
    BOOK_DELETE_SUCCESS(
        "Confirmation de suppression",
        "Le livre a été supprimé avec succès !",
        Alert.AlertType.INFORMATION
    ),
    BOOK_DELETE_FAILURE(
        "Suppression impossible",
        "Il y a encore des emprunts en cours associés à ce livre !",
        Alert.AlertType.WARNING
    ),
    LOAN_ALREADY_EXISTS(
        "Emprunt impossible",
        "L'emprunt existe déjà dans la base de données !",
        Alert.AlertType.WARNING
    ),
    LOAN_OUT_OF_STOCK(
        "Emprunt impossible",
        "Le stock est épuisé !",
        Alert.AlertType.WARNING
    ),
    LOAN_ADD_SUCCESS(
        "Confirmation d'emprunt",
        "Emprunt créé avec succès !",
        Alert.AlertType.INFORMATION
    ),
    LOAN_UPDATE_SUCCESS(
        "Confirmation de retour",
        "Le retour de livre a été effectué avec succès !",
        Alert.AlertType.INFORMATION
    ),
    LOAN_DELETE(
        "Suppression d'un emprunt", "Êtes-vous certain de vouloir supprimer cet emprunt ?",
        Alert.AlertType.WARNING, ButtonType.OK, ButtonType.CANCEL
    ),
    LOAN_DELETE_SUCCESS(
        "Confirmation de suppression",
        "L'emprunt a été supprimé avec succès !",
        Alert.AlertType.INFORMATION
    ),
    LOAN_DELETE_FAILURE(
        "Suppression impossible",
        "Vous devez d'abord rendre les exemplaires de ce livre !",
        Alert.AlertType.WARNING
    ),
    LIBRARY_UPDATE_SUCCESS(
        "Confirmation de modification",
        "Les informations de la librairie ont été modifiées avec succès !",
        Alert.AlertType.INFORMATION
    ),
    DB_FULL(
        "Ajout impossible",
        "La base de données de données est pleine !",
        Alert.AlertType.WARNING
    ),
    UNKNOWN_ERROR(
        "Erreur inconnue",
        "Une erreur inconnue a été rencontrée !",
        Alert.AlertType.WARNING
    );

    private final Alert alert;

    Message(String title, String content, Alert.AlertType type, ButtonType... buttonTypes) {
        alert = new Alert(type, null, buttonTypes);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
    }

    Message(String title, String content, Alert.AlertType type) {
        alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
    }

    public Optional<ButtonType> showAndWait() {
        return alert.showAndWait();
    }
}
