package be.technobel.employees.db;

import be.technobel.employees.model.Contract;

/** Critère de filtre */
public enum ContractType {
    /** Tous les {@link Contract contrats} */
    ALL,

    /** {@link Contract Contrats} CDD */
    FTC,

    /** {@link Contract Contrats} CDI */
    PC,

    /** {@link Contract Contrats} terminés */
    FIRED,

    /** {@link Contract Contrats} inexistants */
    NOT_RECRUITED;
}
