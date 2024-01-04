package be.technobel.aquarium.model;

public abstract class Vivant implements Cloneable {
    public static final int ESPERANCE_VIE = 20;
    public static final int DEMI_VIE = ESPERANCE_VIE/2;
    private int pv = 10;
    private int age = 0;

    public Vivant() {}
    public Vivant(int age) {
        this.age = age;
    }

    public int getPv() {
        return pv;
    }

    public int getAge() {
        return age;
    }

    protected void setPv(int pv) {
        this.pv = pv;
    }

    protected void addPv(int value) {
        if(Integer.MAX_VALUE-pv < value) pv = Integer.MAX_VALUE;
        else if(value < 0 && value < -pv) pv = 0;
        else pv += value;
    }

    public boolean estMort() {
        return age >= ESPERANCE_VIE || pv == 0;
    }

    public CauseMort getCauseMort() {
        if(age >= ESPERANCE_VIE) return CauseMort.VIEILLESSE;
        else if(pv == 0) return CauseMort.DEVORE;
        return CauseMort.TOUJOURS_VIVANT;
    }

    public void prochainTour() {
        if(!estMort()) ++age;
    }

    @Override
    public Vivant clone() {
        try { return (Vivant) super.clone(); }
        catch(CloneNotSupportedException e) { throw new AssertionError(); }
    }

    public enum CauseMort {
        DEVORE("Dévoré"),
        FAIM("Faim"),
        VIEILLESSE("Vieillesse"),
        TOUJOURS_VIVANT("Vivant");
        private final String name;
        public String getName() {
            return name;
        }
        CauseMort(String name) {
            this.name = name;
        }
    }
}
