package be.technobel.heroes_vs_monsters.model;

public class Orc extends Monstre {
    @Override
    public int getForce() {
        return super.getForce()+1;
    }
    @Override
    public int getCuir() {
        return 0;
    }
    public Orc(int x, int y) {
        super(x, y);
    }
    @Override
    public String toString() {
        return "Orc";
    }
}
