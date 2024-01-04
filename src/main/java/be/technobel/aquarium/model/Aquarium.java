package be.technobel.aquarium.model;

import be.technobel.aquarium.model.mock.MockData;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class Aquarium {
    private final List<Vivant> vivants = new ArrayList<>();
    private final Statistiques statistiques = new Statistiques();
    private final MockData mock = new MockData();

    /**
     * Ajoute des algues à l'aquarium
     * @param algues Une ou plusieurs algue(s)
     */
    public void ajouterAlgue(Algue... algues) {
        Collections.addAll(this.vivants, algues);
    }

    /**
     * Ajoute des poissons à l'aquarium
     * @param poissons Une ou plusieurs poisson(s)
     */
    public void ajouterPoisson(Poisson<?>... poissons) {
        Collections.addAll(this.vivants, poissons);
    }

    public Stream<Vivant> getVivants() {
        return vivants.stream();
    }

    public Statistiques getStatistiques() {
        return statistiques;
    }

    /** Retire tous les poissons et algues morts. */
    public void nettoyer() {
        vivants.removeIf(Vivant::estMort);
    }

    /** Génération du prochain tour */
    public void prochainTour() {
        // 1ère initialisation
        if(statistiques.premierTour()) {
            var tour = statistiques.nouveauTour();
            tour.copyDataFrom(vivants.stream());
        }

        // Génération du tour
        var tour = statistiques.nouveauTour();
        List<Vivant> naissances = new ArrayList<>();
        var tmp = new ArrayList<>(vivants);
        Collections.shuffle(tmp);
        tmp.forEach(vivant -> {
            // Gestion de mort de faim ou vieillesse
            boolean etaitMort = vivant.estMort();
            vivant.prochainTour();
            if(vivant.estMort()) {
                if(!etaitMort) tour.logMort(vivant);
                return;
            }

            // ALGUE
            if(vivant instanceof Algue algue) {
                // Une algue qui a 10 PV ou plus se sépare en deux pour donner naissance à une algue deux fois plus petite
                if(algue.getPv() >= 10) {
                    naissances.add(algue.seReproduire());
                    tour.logReproduction(algue);
                }
            }

            // POISSON
            if(!(vivant instanceof Poisson<?> poisson)) return;
            if(poisson.aFaim()) {
                // Un poisson qui a suffisamment faim (5 PV ou moins) cherche à manger.
                if(poisson instanceof PoissonCarnivore poissonC) {
                    // Si aucun autre vivant
                    if(vivants.size() < 2) {
                        tour.logNourritureIndisponible(poisson);
                        return;
                    }
                    // Un carnivore attaque chaque tour un poisson au hasard, il n’a qu’une seule chance.
                    long randomIndex = ThreadLocalRandom.current().nextLong(vivants.size()-1);
                    var nourriture = vivants.stream().filter(v -> v != poisson).skip(randomIndex).findFirst().orElseThrow();
                    if(!nourriture.estMort() && nourriture instanceof Poisson<?> proie) {
                        if(poissonC.manger(proie)) {
                            tour.logManger(poissonC, proie);
                            if(proie.estMort()) tour.logMort(proie);
                        }
                        else tour.logMangerPoissonIncompatible(poissonC, proie);
                    }
                    else tour.logNourritureIndisponible(poisson);
                }
                else if(poisson instanceof PoissonHerbivore poissonH) {
                    long nbAlguesVivantes = vivants.stream().filter(v -> !v.estMort() && v instanceof Algue).count();
                    // Si aucune autre algue
                    if(nbAlguesVivantes == 0) {
                        tour.logNourritureIndisponible(poisson);
                        return;
                    }
                    // Un herbivore trouve automatiquement une algue s'il y en reste.
                    long randomIndex = ThreadLocalRandom.current().nextLong(nbAlguesVivantes);
                    var nourriture = vivants.stream().filter(v -> !v.estMort() && v instanceof Algue)
                        .skip(randomIndex).map(v -> (Algue)v).findFirst();
                    if(nourriture.isPresent()) {
                        Algue algue = nourriture.get();
                        poissonH.manger(algue);
                        tour.logManger(poissonH, algue);
                        if(algue.estMort()) tour.logMort(algue);
                    }
                    else tour.logNourritureIndisponible(poisson);
                }
            } else {
                // Un poisson qui n’a pas faim va aller voir un autre poisson (au hasard).
                long nbPoissonsVivants = vivants.stream().filter(v -> !v.estMort() && v instanceof Poisson<?>).count();
                long randomIndex = ThreadLocalRandom.current().nextLong(nbPoissonsVivants);
                var partenaire = vivants.stream().filter(v -> !v.estMort() && v instanceof Poisson<?> && v != poisson)
                    .skip(randomIndex).map(v -> (Poisson<?>)v).findFirst();
                // Si le poisson a trouvé un partenaire disponible
                if(partenaire.isPresent()) {
                    Poisson<?> partenaireDisponible = partenaire.get();
                    var naissance = poisson.seReproduire(partenaireDisponible);
                    // Si ces poissons sont compatibles, ils se reproduisent et donnent naissance à un troisième poisson.
                    if(naissance.isPresent()) {
                        var enfant = naissance.get();
                        enfant.setNom(mock.firstName());
                        naissances.add(enfant);
                        tour.logReproduction(poisson, partenaireDisponible, enfant);
                    }
                    else tour.logReproductionIncompatible(poisson, partenaireDisponible);
                }
                else tour.logReproductionIndisponible(poisson);
            }
        });
        vivants.addAll(naissances);
        tour.copyDataFrom(vivants.stream());
    }
}
