package be.technobel.aquarium.model;

import be.technobel.aquarium.model.poissons_races.RacePoisson;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Poisson<NOURRITURE extends Vivant> extends Vivant {
    public enum Sexe {MALE, FEMELLE};
    private String nom = "";
    private Sexe sexe;
    private boolean estMortFaim = false;

    public Poisson(String nom, Sexe sexe, int age) {
        super(age);
        this.nom = nom;
        this.sexe = sexe;
    }
    public Poisson(String nom, Sexe sexe) {
        super();
        this.nom = nom;
        this.sexe = sexe;
    }
    public Poisson(Sexe sexe) {
        super();
        this.sexe = sexe;
    }
    public Poisson() {
        super();
        this.sexe = ThreadLocalRandom.current().nextInt(2)%2 == 0 ? Sexe.MALE : Sexe.FEMELLE;
    }

    public String getNom() {
        return nom;
    }
    public Sexe getSexe() {
        return sexe;
    }

    protected void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public boolean aFaim() {
        return !estMort() && getPv() <= 5;
    }

    @Override
    public CauseMort getCauseMort() {
        if(getAge() >= ESPERANCE_VIE) return CauseMort.VIEILLESSE;
        else if(getPv() == 0) return estMortFaim ? CauseMort.FAIM : CauseMort.DEVORE;
        return CauseMort.TOUJOURS_VIVANT;
    }

    @Override
    public void prochainTour() {
        super.prochainTour();
        if(!estMort()) {
            // Gestion de la faim
            addPv(-1);
            if(estMort()) estMortFaim = true;
        }
    }

    @Override
    public Poisson<?> clone() {
        return (Poisson<?>)super.clone();
    }

    public abstract boolean manger(NOURRITURE nourriture);
    public abstract Optional<Poisson<?>> seReproduire(Poisson<?> autre);
    public abstract RacePoisson getRace();
}
