package be.technobel.aquarium.model;

public abstract class PoissonHerbivore extends Poisson<Algue> {
    public PoissonHerbivore() {
        super();
    }
    public PoissonHerbivore(Sexe sexe) {
        super(sexe);
    }
    public PoissonHerbivore(String nom, Sexe sexe) {
        super(nom, sexe);
    }
    public PoissonHerbivore(String nom, Sexe sexe, int age) {
        super(nom, sexe, age);
    }

    @Override
    public boolean manger(Algue algue) {
        algue.addPv(-2);
        this.addPv(3);
        return true;
    }
}
