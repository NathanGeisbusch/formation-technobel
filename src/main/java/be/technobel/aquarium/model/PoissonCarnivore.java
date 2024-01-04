package be.technobel.aquarium.model;

public abstract class PoissonCarnivore extends Poisson<Poisson<?>> {
    public PoissonCarnivore() {
        super();
    }
    public PoissonCarnivore(Sexe sexe) {
        super(sexe);
    }
    public PoissonCarnivore(String nom, Sexe sexe) {
        super(nom, sexe);
    }
    public PoissonCarnivore(String nom, Sexe sexe, int age) {
        super(nom, sexe, age);
    }

    /**
     * Mange un autre poisson.
     * @param poisson Poisson à manger
     * @return false si le poisson en paramètre est le même que celui de l'instance
     *               ou si les poissons sont de la même espèce.
     */
    @Override
    public boolean manger(Poisson<?> poisson) {
        boolean isSameRace = getRace() == poisson.getRace();
        if(poisson == this || isSameRace) return false;
        poisson.addPv(-4);
        this.addPv(5);
        return true;
    }
}
