package be.technobel.employees.db;

import be.technobel.employees.model.Employee;
import java.math.BigDecimal;
import java.util.Optional;

public interface EmployeeDatabase {
    /**
     * Récupère une liste d'employés.
     * @param contractType Critère de filtre (type de contrat)
     * @param skip Nombre d'éléments à passer
     * @param limit Nombre d'éléments à renvoyer
     * @param sortBy Critère(s) de tri
     * @return Les {@link Employee employés} correspondant aux critères de recherche
     */
    Employee[] get(ContractType contractType, long skip, long limit, EmployeeSortBy... sortBy);

    /**
     * Récupère un employé par son identifiant.
     * @param id identifiant de l'{@link Employee employé}
     * @return L'{@link Employee employé} correspondant à l'id
     */
    Optional<Employee> getById(long id);

    /**
     * Ajoute un employé à la db.
     * @param employee L'{@link Employee employé} à ajouter
     * @return true si l'ajout a pu être effectué, false s'il n'y a plus d'id disponible
     */
    boolean add(Employee employee);

    /**
     * Modifie un employé dans la db.
     * @param employee L'{@link Employee employé} à modifier
     * @return true si la modification a pu être effectuée, false si l'employé n'existe pas
     */
    boolean update(Employee employee);

    /**
     * Supprime un employé dans la db.
     * @param employee L'{@link Employee employé} à supprimer
     * @return true si la suppression a pu être effectuée, false si l'employé n'existe pas
     */
    boolean delete(Employee employee);

    /** @return La moyenne des salaires de tous les employés actifs */
    BigDecimal getAvgSalary();

    /** @return Le total des salaires de tous les employés actifs*/
    BigDecimal getTotalSalary();
}
