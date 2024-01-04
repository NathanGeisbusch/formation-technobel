package be.technobel.aquarium.model.poissons_races;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.PoissonCarnivore;
import java.util.Optional;

public class Thon extends PoissonCarnivore {
    public Thon() {
        super();
    }
    public Thon(Sexe sexe) {
        super(sexe);
    }
    public Thon(String nom, Sexe sexe) {
        super(nom, sexe);
    }
    public Thon(String nom, Sexe sexe, int age) {
        super(nom, sexe, age);
    }

    @Override
    public Optional<Poisson<?>> seReproduire(Poisson<?> autre) {
        if(autre instanceof Thon && getSexe() != autre.getSexe()) return Optional.of(new Thon());
        return Optional.empty();
    }

    @Override
    public RacePoisson getRace() {
        return RacePoisson.THON;
    }
}
