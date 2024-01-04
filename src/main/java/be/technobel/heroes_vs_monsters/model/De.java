package be.technobel.heroes_vs_monsters.model;

import java.util.concurrent.ThreadLocalRandom;

public enum De {
    DE2(1,3),
    DE3(1,4),
    DE4(1,5),
    DE6(1,7),
    DE12(1,13);
    private final int min;
    private final int max;
    De(int min, int max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }
    public int lance() {
        return ThreadLocalRandom.current().nextInt(this.min, this.max);
    }
    public static int lance(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
