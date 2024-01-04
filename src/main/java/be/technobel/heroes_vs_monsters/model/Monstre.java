package be.technobel.heroes_vs_monsters.model;

public abstract class Monstre extends Personnage {
    private final int or;
    private final int cuir;
    public int getOr() {
        return this.or;
    }
    public int getCuir() {
        return this.cuir;
    }
    protected Monstre(int x, int y) {
        super(x, y);
        this.or = De.DE6.lance();
        this.cuir = De.DE4.lance();
    }
}
