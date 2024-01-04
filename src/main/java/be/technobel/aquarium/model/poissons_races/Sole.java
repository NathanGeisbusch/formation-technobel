package be.technobel.aquarium.model.poissons_races;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.PoissonHerbivore;
import java.util.Optional;

public class Sole extends PoissonHerbivore {
    public Sole() {
        super();
    }
    public Sole(Sexe sexe) {
        super(sexe);
    }
    public Sole(String nom, Sexe sexe) {
        super(nom, sexe);
    }
    public Sole(String nom, Sexe sexe, int age) {
        super(nom, sexe, age);
    }

    @Override
    public Optional<Poisson<?>> seReproduire(Poisson<?> autre) {
        if(autre instanceof Sole) {
            setSexe(autre.getSexe() == Sexe.MALE ? Sexe.FEMELLE : Sexe.MALE);
            return Optional.of(new Sole());
        }
        return Optional.empty();
    }

    @Override
    public RacePoisson getRace() {
        return RacePoisson.SOLE;
    }
}
