package be.technobel.heroes_vs_monsters.model;

public class Nain extends Hero {
    @Override
    public int getEndurance() {
        return super.getEndurance()+2;
    }
    public Nain(int x, int y) {
        super(x, y);
    }
    @Override
    public String toString() {
        return "Nain";
    }
}
