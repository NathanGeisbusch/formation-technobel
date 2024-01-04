package be.technobel.heroes_vs_monsters.model;

public class Dragon extends Monstre {
    @Override
    public int getEndurance() {
        return super.getEndurance()+1;
    }
    public Dragon(int x, int y) {
        super(x, y);
    }
    @Override
    public String toString() {
        return "Dragon";
    }
}
