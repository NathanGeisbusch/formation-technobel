package be.technobel.heroes_vs_monsters.model;

public abstract class Hero extends Personnage {
    private int or = 0;
    private int cuir = 0;
    public int getOr() {
        return this.or;
    }
    public int getCuir() {
        return this.cuir;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
    protected Hero(int x, int y) {
        super(x, y);
    }
    public void addOr(int or) {
        this.or = Math.max(this.or + or, 0);
    }
    public void addCuir(int cuir) {
        this.cuir = Math.max(this.cuir + cuir, 0);
    }
    public void restaurerPv() {
        this.pvRestants = this.getPv();
    }
    public int getPv() {
        return super.getPv()*3/2; //rééquilibrage
    }
    @Override
    public String toString() {
        return "H";
    }

}
