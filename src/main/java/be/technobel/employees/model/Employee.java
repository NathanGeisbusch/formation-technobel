package be.technobel.employees.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class Employee implements Cloneable {
    private static final BigDecimal defaultPercentBonusPerYear = new BigDecimal("0.02");
    private long id = 0;
    private String lastName = "";
    private String firstName = "";
    private LocalDate birthdate = null;
    private BigDecimal salaryBase;
    private List<Contract> contracts;

    /** Constructeur par défaut */
    public Employee() {
        this.salaryBase = BigDecimal.ZERO;
        this.contracts = new ArrayList<>();
    }

    /**
     * @param lastName Nom de famille
     * @param firstName Prénom
     * @param birthdate Date de naissance
     * @param salaryBase Salaire de base
     */
    public Employee(String lastName, String firstName, LocalDate birthdate, BigDecimal salaryBase) {
        assert lastName != null : new NullPointerException("lastName");
        assert firstName != null : new NullPointerException("firstName");
        assert birthdate != null : new NullPointerException("birthdate");
        assert salaryBase != null : new NullPointerException("salaryBase");
        assert salaryBase.compareTo(BigDecimal.ZERO) >= 0 : new IllegalArgumentException("salaryBase");
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthdate = birthdate;
        this.salaryBase = salaryBase;
        this.contracts = new ArrayList<>();
    }

    /** @return identifiant de l'{@link Employee employé} */
    public long getId() {
        return id;
    }

    /** @param id identifiant de l'{@link Employee employé}
     *            (Un identifiant valide est supérieur à 0)*/
    public Employee setId(long id) {
        assert id >= 0 : new IllegalArgumentException("id");
        this.id = id;
        return this;
    }

    /** @return Nom de famille */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName Nom de famille
     * @return Instance de l'objet
     */
    public Employee setLastName(String lastName) {
        assert lastName != null : new NullPointerException("lastName");
        this.lastName = lastName;
        return this;
    }

    /** @return Prénom */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName Prénom
     * @return Instance de l'objet
     */
    public Employee setFirstName(String firstName) {
        assert firstName != null : new NullPointerException("firstName");
        this.firstName = firstName;
        return this;
    }

    /** @return Nom complet */
    public String getFullName(){
        return this.getLastName() + " " + this.getFirstName();
    }

    /** @return Date de naissance */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /**
     * @param birthdate Date de naissance
     * @return Instance de l'objet
     */
    public Employee setBirthdate(LocalDate birthdate) {
        assert birthdate != null : new NullPointerException("birthdate");
        this.birthdate = birthdate;
        return this;
    }

    /** @return Salaire de base */
    public BigDecimal getSalaryBase(){
        return this.salaryBase;
    }

    /**
     * @param salaryBase Salaire de base
     * @return Instance de l'objet
     */
    public Employee setSalaryBase(BigDecimal salaryBase) {
        assert salaryBase != null : new NullPointerException("salaryBase");
        assert salaryBase.compareTo(BigDecimal.ZERO) >= 0 : new IllegalArgumentException("salaryBase");
        this.salaryBase = salaryBase;
        return this;
    }

    /**
     * @param salaryBase Salaire de base
     * @return Instance de l'objet
     */
    public Employee setSalaryBase(long salaryBase) {
        assert salaryBase >= 0 : new IllegalArgumentException("salaryBase");
        this.salaryBase = new BigDecimal(salaryBase);
        return this;
    }

    /**
     * Calcule le salaire majoré (par défaut : 2% par année d'ancienneté si CDI)
     * @return Salaire majoré si CDI, {@link Employee#salaryBase salaire de base} si CDD, sinon 0
     */
    public BigDecimal getSalary() {
        return this.getSalary(defaultPercentBonusPerYear);
    }

    /**
     * Calcule le salaire majoré
     * @param percentBonusPerYear Pourcentage bonus par année d'ancienneté si CDI
     * @return Salaire majoré si CDI, {@link Employee#salaryBase salaire de base} si CDD, sinon 0
     */
    public BigDecimal getSalary(BigDecimal percentBonusPerYear) {
        assert percentBonusPerYear.compareTo(BigDecimal.ZERO) >= 0 : new IllegalArgumentException("percentBonusPerYear");
        if(this.isPC()) {
            long seniority = this.contracts.get(this.contracts.size()-1).getSeniority();
            BigDecimal result = this.salaryBase;
            for(int i = 0; i < seniority; i++) {
                result = result.add(result.multiply(percentBonusPerYear));
            }
            return result;
        }
        else if(this.isFired() || !this.isRecruited()) return BigDecimal.ZERO;
        else return this.getSalaryBase();
    }

    /** @return true si l'{@link Employee employé} est en CDD */
    public boolean isFTC() {
        return contracts.size() != 0 && contracts.get(contracts.size()-1).isFTC();
    }

    /** @return true si l'{@link Employee employé} est en CDI */
    public boolean isPC() {
        return contracts.size() != 0 && contracts.get(contracts.size()-1).isPC();
    }

    /** @return true si l'{@link Employee employé} est licencié */
    public boolean isFired() {
        return contracts.size() != 0 && contracts.get(contracts.size()-1).isFinished();
    }

    /** @return true si l'{@link Employee employé} a minimum 1 {@link Contract contrat} */
    public boolean isRecruited() {
        return contracts.size() != 0;
    }

    /** @return Nombre de {@link Contract contrats} assignés à l'{@link Employee employé} */
    public int getContractsAmount(){
        return contracts.size();
    }

    /** @return Les {@link Contract contrats} de l'{@link Employee employé} sous forme de {@link Stream} */
    public Stream<Contract> getContracts() {
        return contracts.stream();
    }

    /**
     * Recherche un contrat dont la date de début est égale à celle fournie en paramètre.
     * @param startDate La {@link Contract#getStartDate() date de début} à rechercher
     * @return Le {@link Contract contrat} correspondant à la {@link Contract#getStartDate() date de début}
     */
    public Optional<Contract> findContract(LocalDate startDate) {
        assert startDate != null : new NullPointerException("startDate");
        return contracts.stream().filter(c -> c.getStartDate().equals(startDate)).findFirst();
    }

    /**
     * Assigne un contrat à l'employé.
     * @param contract {@link Contract Contrat} à assigner (la {@link Contract#getStartDate() date de début} du {@link Contract contrat}
     *                sert d'identifiant, donc doit être unique pour un même {@link Employee employé})
     * @return Instance de l'objet
     */
    public Employee addContract(Contract contract) {
        assert contract != null : new NullPointerException("contract");
        this.contracts.add(contract);
        this.contracts.sort(Comparator.comparing(Contract::getStartDate));
        return this;
    }

    /**
     * Retirer le contrat de l'employé.
     * @param contract {@link Contract Contrat} à retirer
     * @return Instance de l'objet
     */
    public Employee removeContract(Contract contract) {
        assert contract != null : new NullPointerException("contract");
        this.contracts.removeIf(c -> c.getStartDate().equals(contract.getStartDate()));
        return this;
    }

    /**
     * Crée une copie de l'employé.
     * @return Copie de l'{@link Employee employé}
     */
    @Override
    public Employee clone() {
        try {
            Employee clone = (Employee)super.clone();
            clone.contracts = new ArrayList<>(contracts);
            return clone;
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
