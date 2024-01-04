package be.technobel.aquarium.model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Statistiques {
    public record Stat<T>(T key, long value) {}

    private final List<Tour> tours = new ArrayList<>();

    /**
     * @return true si aucun tour n'a été créé
     */
    public boolean premierTour() {
        return tours.size() == 0;
    }

    /**
     * Crée et renvoie un nouveau tour.
     * @return le tour créé
     */
    public Tour nouveauTour() {
        Tour tour = new Tour();
        tours.add(tour);
        return tour;
    }

    /**
     * @return le nombre de tours créés
     */
    public int nbTours() {
        return tours.size();
    }

    /**
     * @return Le tour actuel
     * @throws IndexOutOfBoundsException si aucun tour créé.
     */
    public Tour getTourActuel() throws IndexOutOfBoundsException {
        if(tours.size() == 0) throw new IndexOutOfBoundsException();
        return tours.get(tours.size()-1);
    }

    /**
     * @param numero Numéro du tour
     * @return Le tour à ce numéro
     * @throws IndexOutOfBoundsException si aucun tour créé ou index hors des bornes.
     */
    public Tour getTour(int numero) throws IndexOutOfBoundsException {
        if(numero < 0 || numero >= tours.size()) throw new IndexOutOfBoundsException();
        return tours.get(numero);
    }

    /**
     * @return Le nombre de poissons morts pour chaque tour
     */
    public Queue<Stat<Long>> getPoissonsMorts() {
        Queue<Stat<Long>> stats = new ArrayDeque<>();
        for(int tour = 0; tour < tours.size(); tour++) {
            long morts = tours.get(tour).getLogs().filter(l ->
                l instanceof LogMort logMort && logMort.vivant instanceof Poisson<?> poisson && poisson.estMort()
            ).count();
            stats.add(new Stat<>((long)tour, morts));
        }
        return stats;
    }

    /**
     * @return Le nombre d'algues mortes pour chaque tour
     */
    public Queue<Stat<Long>> getAlguesMortes() {
        Queue<Stat<Long>> stats = new ArrayDeque<>();
        for(int tour = 0; tour < tours.size(); tour++) {
            long morts = tours.get(tour).getLogs().filter(l ->
                l instanceof LogMort logMort && logMort.vivant instanceof Algue poisson && poisson.estMort()
            ).count();
            stats.add(new Stat<>((long)tour, morts));
        }
        return stats;
    }

    /**
     * @return Le nombre de poissons nés pour chaque tour
     */
    public Queue<Stat<Long>> getNaissancesPoissons() {
        Queue<Stat<Long>> stats = new ArrayDeque<>();
        for(int tour = 0; tour < tours.size(); tour++) {
            long morts = tours.get(tour).getLogs().filter(v -> v instanceof LogReproductionPoisson).count();
            stats.add(new Stat<>((long)tour, morts));
        }
        return stats;
    }

    /**
     * @return Le nombre d'algues nées pour chaque tour
     */
    public Queue<Stat<Long>> getNaissancesAlgues() {
        Queue<Stat<Long>> stats = new ArrayDeque<>();
        for(int tour = 0; tour < tours.size(); tour++) {
            long morts = tours.get(tour).getLogs().filter(v -> v instanceof LogReproductionAlgue).count();
            stats.add(new Stat<>((long)tour, morts));
        }
        return stats;
    }

    /**
     * @return La répartition des causes de mort
     */
    public Queue<Stat<Vivant.CauseMort>> getCausesMorts() {
        return tours.stream().flatMap(Tour::getLogs)
            .filter(l -> l instanceof LogMort)
            .map(l -> ((LogMort)l).vivant.getCauseMort())
            .collect(groupingBy(Function.identity(), counting()))
            .entrySet().stream()
                .map(entry -> new Stat<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    //----------------------------------------------------
    public static class Tour {
        private final Queue<Log> logs = new ArrayDeque<>();
        private List<Vivant> vivants = List.of();
        public void copyDataFrom(Stream<Vivant> vivants) {
            this.vivants = vivants.map(Vivant::clone).collect(Collectors.toCollection(ArrayList::new));
        }
        public void logMort(Vivant vivant) {
            logs.add(new LogMort(vivant.clone()));
        }
        public void logReproduction(Algue algue) {
            logs.add(new LogReproductionAlgue(algue.clone()));
        }
        public void logReproduction(Poisson<?> poisson1, Poisson<?> poisson2, Poisson<?> enfant) {
            logs.add(new LogReproductionPoisson(poisson1.clone(), poisson2.clone(), enfant.clone()));
        }
        public void logReproductionIncompatible(Poisson<?> poisson1, Poisson<?> poisson2) {
            logs.add(new LogReproductionPoissonIncompatible(poisson1.clone(), poisson2.clone()));
        }
        public void logReproductionIndisponible(Poisson<?> poisson) {
            logs.add(new LogReproductionPoissonIndisponible(poisson.clone()));
        }
        public void logManger(Poisson<?> poisson, Algue algue) {
            logs.add(new LogMangerAlgue(poisson.clone(), algue.clone()));
        }
        public void logManger(Poisson<?> poisson1, Poisson<?> poisson2) {
            logs.add(new LogMangerPoisson(poisson1.clone(), poisson2.clone()));
        }
        public void logMangerPoissonIncompatible(Poisson<?> poisson1, Poisson<?> poisson2) {
            logs.add(new LogMangerPoissonIncompatible(poisson1.clone(), poisson2.clone()));
        }
        public void logNourritureIndisponible(Poisson<?> poisson) {
            logs.add(new LogNourritureIndisponible(poisson.clone()));
        }
        public Stream<Log> getLogs() {
            return logs.stream();
        }
        public Stream<Vivant> getVivants() {
            return vivants.stream();
        }
    }

    //----------------------------------------------------
    public interface Log {
        String toString();
    }
    public record LogMort(Vivant vivant) implements Log {
        public String toString() {
            if(vivant instanceof Poisson<?> poisson) {
                return String.format("%s (%s) est mort (%s).",
                    poisson.getNom().isBlank() ? "Un poisson" : poisson.getNom(),
                    poisson.getRace().getName().toLowerCase(),
                    poisson.getCauseMort().getName().toLowerCase()
                );
            }
            else if(vivant instanceof Algue algue) {
                return String.format("Une algue est morte (%s).",
                    algue.getCauseMort().getName().toLowerCase()
                );
            }
            return "Un être vivant est mort.";
        }
    }
    public record LogReproductionAlgue(Algue algue) implements Log {
        public String toString() {
            return "Un algue s'est reproduite.";
        }
    }
    public record LogReproductionPoisson(Poisson<?> poisson1, Poisson<?> poisson2, Poisson<?> enfant) implements Log {
        public String toString() {
            return String.format("%s (%s) et %s (%s) ont donné naissance à %s.",
                poisson1.getNom().isBlank() ? "Un poisson" : poisson1.getNom(),
                poisson1.getRace().getName().toLowerCase(),
                poisson2.getNom().isBlank() ? "un autre poisson" : poisson2.getNom(),
                poisson2.getRace().getName().toLowerCase(),
                enfant.getNom()
            );
        }
    }
    public record LogReproductionPoissonIncompatible(Poisson<?> poisson1, Poisson<?> poisson2) implements Log {
        public String toString() {
            return String.format("%s (%s) n'est pas compatible à la reproduction avec %s (%s).",
                poisson1.getNom().isBlank() ? "Un poisson" : poisson1.getNom(),
                poisson1.getRace().getName().toLowerCase(),
                poisson2.getNom().isBlank() ? "un autre poisson" : poisson2.getNom(),
                poisson2.getRace().getName().toLowerCase()
            );
        }
    }
    public record LogReproductionPoissonIndisponible(Poisson<?> poisson) implements Log {
        public String toString() {
            return String.format("%s (%s) n'a pas trouvé de partenaire pour se reproduire.",
                poisson.getNom().isBlank() ? "Un poisson" : poisson.getNom(),
                poisson.getRace().getName().toLowerCase()
            );
        }
    }
    public record LogMangerAlgue(Poisson<?> poisson, Algue algue) implements Log {
        public String toString() {
            return String.format("%s (%s) mange une algue.",
                poisson.getNom().isBlank() ? "Un poisson" : poisson.getNom(),
                poisson.getRace().getName().toLowerCase()
            );
        }
    }
    public record LogMangerPoisson(Poisson<?> poisson1, Poisson<?> poisson2) implements Log {
        public String toString() {
            return String.format("%s (%s) mange %s (%s).",
                poisson1.getNom().isBlank() ? "Un poisson" : poisson1.getNom(),
                poisson1.getRace().getName().toLowerCase(),
                poisson2.getNom().isBlank() ? "un autre poisson" : poisson2.getNom(),
                poisson2.getRace().getName().toLowerCase()
            );
        }
    }
    public record LogMangerPoissonIncompatible(Poisson<?> poisson1, Poisson<?> poisson2) implements Log {
        public String toString() {
            return String.format("%s (%s) ne peut pas pas manger %s (%s)%s.",
                poisson1.getNom().isBlank() ? "Un poisson" : poisson1.getNom(),
                poisson1.getRace().getName().toLowerCase(),
                poisson2.getNom().isBlank() ? "un autre poisson" : poisson2.getNom(),
                poisson2.getRace().getName().toLowerCase(),
                poisson1.getRace() == poisson2.getRace() ? " car ils sont de la même espèce" : ""
            );
        }
    }
    public record LogNourritureIndisponible(Poisson<?> poisson) implements Log {
        public String toString() {
            return String.format("%s (%s) n'a pas trouvé de nourriture.",
                poisson.getNom().isBlank() ? "Un poisson" : poisson.getNom(),
                poisson.getRace().getName().toLowerCase()
            );
        }
    }
}
