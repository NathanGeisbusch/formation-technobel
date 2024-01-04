package be.technobel.aquarium.model;

public class Algue extends Vivant {
    public Algue() {
        super();
    }
    public Algue(int age) {
        super(age);
        this.addPv(age);
    }

    public Algue seReproduire() throws IllegalStateException {
        if(estMort()) throw new IllegalStateException();
        if(getPv() < 10) throw new IllegalStateException();
        int newPv = getPv()/2;
        setPv(newPv);
        var algue = new Algue();
        algue.setPv(newPv);
        return algue;
    }

    @Override
    public void prochainTour() {
        super.prochainTour();
        if(!estMort()) this.addPv(1);
    }

    @Override
    public Algue clone() {
        return (Algue)super.clone();
    }
}
