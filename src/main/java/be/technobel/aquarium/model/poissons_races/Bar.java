package be.technobel.aquarium.model.poissons_races;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.PoissonHerbivore;
import java.util.Optional;

public class Bar extends PoissonHerbivore {
    public Bar() {
        super(Sexe.MALE);
    }
    public Bar(String nom) {
        super(nom, Sexe.MALE);
    }
    public Bar(String nom, int age) {
        super(nom, Sexe.MALE, age);
    }

    @Override
    public Sexe getSexe() {
        return getAge() < DEMI_VIE ? Sexe.MALE : Sexe.FEMELLE;
    }

    @Override
    public Optional<Poisson<?>> seReproduire(Poisson<?> autre) {
        if(autre instanceof Bar && getSexe() != autre.getSexe()) return Optional.of(new Bar());
        return Optional.empty();
    }

    @Override
    public RacePoisson getRace() {
        return RacePoisson.BAR;
    }
}
