package be.technobel.aquarium.model.poissons_races;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.PoissonHerbivore;
import java.util.Optional;

public class Carpe extends PoissonHerbivore {
    public Carpe() {
        super();
    }
    public Carpe(Sexe sexe) {
        super(sexe);
    }
    public Carpe(String nom, Sexe sexe) {
        super(nom, sexe);
    }
    public Carpe(String nom, Sexe sexe, int age) {
        super(nom, sexe, age);
    }

    @Override
    public Optional<Poisson<?>> seReproduire(Poisson<?> autre) {
        if(autre instanceof Carpe && getSexe() != autre.getSexe()) return Optional.of(new Carpe());
        return Optional.empty();
    }

    @Override
    public RacePoisson getRace() {
        return RacePoisson.CARPE;
    }
}
