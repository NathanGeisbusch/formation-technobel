package be.technobel.aquarium.model.poissons_races;

import be.technobel.aquarium.model.Poisson;
import be.technobel.aquarium.model.PoissonCarnivore;
import java.util.Optional;

public class Merou extends PoissonCarnivore {
    public Merou() {
        super(Sexe.MALE);
    }
    public Merou(String nom) {
        super(nom, Sexe.MALE);
    }
    public Merou(String nom, int age) {
        super(nom, Sexe.MALE, age);
    }

    @Override
    public Sexe getSexe() {
        return getAge() < DEMI_VIE ? Sexe.MALE : Sexe.FEMELLE;
    }

    @Override
    public Optional<Poisson<?>> seReproduire(Poisson<?> autre) {
        if(autre instanceof Merou && getSexe() != autre.getSexe()) return Optional.of(new Merou());
        return Optional.empty();
    }

    @Override
    public RacePoisson getRace() {
        return RacePoisson.MEROU;
    }
}
