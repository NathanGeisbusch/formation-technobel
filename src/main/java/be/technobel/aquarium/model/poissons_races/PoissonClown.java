package be.technobel.aquarium.model.poissons_races;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.PoissonCarnivore;
import java.util.Optional;

public class PoissonClown extends PoissonCarnivore {
    public PoissonClown() {
        super();
    }
    public PoissonClown(Sexe sexe) {
        super(sexe);
    }
    public PoissonClown(String nom, Sexe sexe) {
        super(nom, sexe);
    }
    public PoissonClown(String nom, Sexe sexe, int age) {
        super(nom, sexe, age);
    }

    @Override
    public Optional<Poisson<?>> seReproduire(Poisson<?> autre) {
        if(autre instanceof PoissonClown) {
            setSexe(autre.getSexe() == Sexe.MALE ? Sexe.FEMELLE : Sexe.MALE);
            return Optional.of(new PoissonClown());
        }
        return Optional.empty();
    }

    @Override
    public RacePoisson getRace() {
        return RacePoisson.CLOWN;
    }
}
