package be.technobel.employees.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Contract implements Cloneable {
    private LocalDate startDate;

    /** Optional */
    private LocalDate endDate;

    /**
     * Constructeur d'un CDD
     * @param startDate Date de début
     * @param endDate Date de fin (doit être supérieure ou égale à la date de début,
     *                peut être null pour indiquer que le {@link Contract contrat} est un CDI)
     */
    public Contract(LocalDate startDate, LocalDate endDate) {
        assert startDate != null : new NullPointerException("startDate");
        assert endDate == null || !endDate.isBefore(startDate) : new IllegalArgumentException("endDate");
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Constructeur d'un CDI
     * @param startDate Date de début
     */
    public Contract(LocalDate startDate) {
        assert startDate != null : new NullPointerException("startDate");
        this.startDate = startDate;
        this.endDate = null;
    }

    /** Constructeur par défaut */
    public Contract() {
        this.startDate = LocalDate.now();
        this.endDate = null;
    }

    /** @return Date de début */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate Date de début (doit être inférieure ou égale à la date de fin)
     * @return Instance de l'objet
     */
    public Contract setStartDate(LocalDate startDate) {
        assert startDate != null : new NullPointerException("startDate");
        assert endDate == null || !startDate.isAfter(endDate): new IllegalArgumentException("startDate");
        this.startDate = startDate;
        return this;
    }

    /** @return Date de fin */
    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    /**
     * @param endDate Date de fin (doit être supérieure ou égale à la date de début,
     *                peut être null pour indiquer que le {@link Contract contrat} est un CDI)
     * @return Instance de l'objet
     */
    public Contract setEndDate(LocalDate endDate) {
        assert endDate == null || !endDate.isBefore(startDate) : new IllegalArgumentException("endDate");
        this.endDate = endDate;
        return this;
    }

    /** @return true si {@link Contract contrat} CDD */
    public boolean isFTC() {
        return endDate != null && !LocalDate.now().isAfter(endDate);
    }

    /** @return true si {@link Contract contrat} CDI */
    public boolean isPC() {
        return endDate == null;
    }

    /** @return true si {@link Contract contrat} terminé */
    public boolean isFinished() {
        return endDate != null && LocalDate.now().isAfter(endDate);
    }

    /** @return Années d'ancienneté */
    public long getSeniority() {
        return ChronoUnit.YEARS.between(startDate , LocalDate.now());
    }

    /**
     * Crée une copie du contrat.
     * @return Copie du {@link Contract contrat}
     */
    @Override
    public Contract clone() {
        try {
            return (Contract)super.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}