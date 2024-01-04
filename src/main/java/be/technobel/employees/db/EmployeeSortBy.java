package be.technobel.employees.db;

/** Critères de tri */
public enum EmployeeSortBy {
	/** Nom complet par ordre croissant */
    FULL_NAME_ASC,

	/** Nom complet par ordre décroissant */
	FULL_NAME_DSC,

	/** Nom de famille par ordre croissant */
	LAST_NAME_ASC,

	/** Nom de famille par ordre décroissant */
	LAST_NAME_DSC,

	/** Prénom par ordre croissant */
	FIRST_NAME_ASC,

	/** Prénom par ordre décroissant */
	FIRST_NAME_DSC,

	/** Date de naissance par ordre croissant */
	BIRTH_DATE_ASC,

	/** Date de naissance par ordre décroissant */
	BIRTH_DATE_DSC,

	/** Salaire par ordre croissant */
	SALARY_ASC,

	/** Salaire par ordre décroissant */
	SALARY_DSC;
}
