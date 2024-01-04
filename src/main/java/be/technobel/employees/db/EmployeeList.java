package be.technobel.employees.db;

import be.technobel.employees.model.Employee;
import static be.technobel.employees.db.BigDecimalAverageCollector.avgBigDecimal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/** Implémentation d'une base de données d'{@link Employee employés} sous forme de {@link List liste} en mémoire. */
public class EmployeeList implements EmployeeDatabase {
    private final List<Employee> employees = new ArrayList<>();
    private int maxID = 0;

    @Override
    public Employee[] get(ContractType contractType, long skip, long limit, EmployeeSortBy... sortBy) {
        assert contractType != null : new NullPointerException("contractType");
        assert sortBy != null : new NullPointerException("sortBy");
        assert skip >= 0 : new IllegalArgumentException("skip");
        assert limit >= 0 : new IllegalArgumentException("limit");
        Stream<Employee> stream = this.employees.stream().filter(x -> switch(contractType) {
            case ALL -> true;
            case FTC -> x.isFTC();
            case PC -> x.isPC();
            case FIRED -> x.isFired();
            case NOT_RECRUITED -> !x.isRecruited();
        }).sorted((a,b) -> {
            for(EmployeeSortBy field : sortBy) {
                int compare = switch(field) {
                    case FIRST_NAME_ASC ->  a.getFirstName().toLowerCase().compareTo(b.getFirstName().toLowerCase());
                    case FIRST_NAME_DSC -> -a.getFirstName().toLowerCase().compareTo(b.getFirstName().toLowerCase());
                    case LAST_NAME_ASC  ->  a.getLastName().toLowerCase().compareTo(b.getLastName().toLowerCase());
                    case LAST_NAME_DSC  -> -a.getLastName().toLowerCase().compareTo(b.getLastName().toLowerCase());
                    case FULL_NAME_ASC  ->  a.getFullName().toLowerCase().compareTo(b.getFullName().toLowerCase());
                    case FULL_NAME_DSC  -> -a.getFullName().toLowerCase().compareTo(b.getFullName().toLowerCase());
                    case BIRTH_DATE_ASC ->  a.getBirthdate().compareTo(b.getBirthdate());
                    case BIRTH_DATE_DSC -> -a.getBirthdate().compareTo(b.getBirthdate());
                    case SALARY_ASC     ->  a.getSalary().compareTo(b.getSalary());
                    case SALARY_DSC     -> -a.getSalary().compareTo(b.getSalary());
                };
                if(compare != 0) return compare;
            }
            return 0;
        });
        if(skip > 0) stream = stream.skip(skip);
        if(limit > 0) stream = stream.limit(limit);
        return stream.map(Employee::clone).toArray(Employee[]::new);
    }

    @Override
    public Optional<Employee> getById(long id) {
        assert id >= 0 : new IllegalArgumentException("id");
        return this.employees.stream()
            .filter(e -> id == e.getId())
            .findFirst()
            .map(Employee::clone);
    }

    @Override
    public boolean add(Employee employee) {
        assert employee != null : new NullPointerException("employee");
        if(maxID == Integer.MAX_VALUE) return false;
        employees.add(employee.setId(++maxID));
        return true;
    }

    @Override
    public boolean update(Employee employee) {
        assert employee != null : new NullPointerException("employee");
        for(int index = 0; index < this.employees.size(); index++) {
            Employee e = this.employees.get(index);
            if(e.getId() == employee.getId()) {
                this.employees.set(index, employee);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Employee employee) {
        assert employee != null : new NullPointerException("employee");
        for(int index = 0; index < this.employees.size(); index++) {
            Employee e = this.employees.get(index);
            if(e.getId() == employee.getId()) {
                this.employees.remove(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public BigDecimal getAvgSalary() {
        return this.employees.stream()
            .filter(x -> !x.isFired() && x.isRecruited())
            .map(Employee::getSalary)
            .collect(avgBigDecimal());
    }

    @Override
    public BigDecimal getTotalSalary() {
        return this.employees.stream()
            .filter(x -> !x.isFired() && x.isRecruited())
            .map(Employee::getSalary)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
    }
}
