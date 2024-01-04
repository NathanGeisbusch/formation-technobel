package be.technobel.heroes_vs_monsters.model;

public class Humain extends Hero {
    @Override
    public int getForce() {
        return super.getForce()+1;
    }
    @Override
    public int getEndurance() {
        return super.getEndurance()+1;
    }
    public Humain(int x, int y) {
        super(x, y);
    }
    @Override
    public String toString() {
        return "Humain";
    }
}
