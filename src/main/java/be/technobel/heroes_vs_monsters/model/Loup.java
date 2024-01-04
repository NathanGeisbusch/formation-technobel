package be.technobel.heroes_vs_monsters.model;

public class Loup extends Monstre {
    @Override
    public int getOr() {
        return 0;
    }
    public Loup(int x, int y) {
        super(x, y);
    }
    @Override
    public String toString() {
        return "Loup";
    }
}
