package be.technobel.heroes_vs_monsters.model;

public abstract class Personnage {
    private final int endurance;
    private final int force;
    private final int pv;
    protected int pvRestants;
    protected int x;
    protected int y;
    public int getEndurance() {
        return this.endurance;
    }
    public int getForce() {
        return this.force;
    }
    public int getPv() {
        return this.pv;
    }
    public int getPvRestants() {
        return this.pvRestants;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    protected Personnage(int x, int y) {
        this.endurance = Personnage.get3MaxOf4De6();
        this.force = Personnage.get3MaxOf4De6();
        this.pv = this.getEndurance() + Personnage.modificateur(this.getEndurance());
        this.pvRestants = this.getPv();
        this.x = x;
        this.y = y;
    }
    public int frappe(Personnage p) {
        int dmg = Math.min(p.pvRestants, De.DE4.lance() + Personnage.modificateur(this.getForce()));
        p.pvRestants -= dmg;
        return dmg;
    }
    public boolean estMort() {
        return this.pvRestants == 0;
    }
    private static int modificateur(int value) {
        return value < 5 ? -1 : value < 10 ? 0 : value < 15 ? 1 : 2;
    }
    private static int get3MaxOf4De6() {
        int roll1  = De.DE6.lance(), roll2  = De.DE6.lance(), roll3  = De.DE6.lance(), roll4  = De.DE6.lance();
        if(roll1<roll2) roll1 = roll2^roll1^(roll2=roll1);
        if(roll3<roll4) roll3 = roll4^roll3^(roll4=roll3);
        if(roll1<roll3) roll1 = roll3^roll1^(roll3=roll1);
        if(roll2<roll4) roll2 = roll4^roll2^(roll4=roll2);
        if(roll2<roll3) roll2 = roll3^roll2^(roll3=roll2);
        return roll1+roll2+roll3;
    }
}
