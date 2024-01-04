package be.technobel.aquarium.model.poissons_races;

public enum RacePoisson {
    BAR("Bar"),
    CARPE("Carpe"),
    MEROU("Mérou"),
    CLOWN("Poisson-clown"),
    SOLE("Sole"),
    THON("Thon");

    private final String name;

    RacePoisson(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RacePoisson fromName(String name) {
        for(RacePoisson race : RacePoisson.values()) {
            if(race.getName().equals(name)) return race;
        }
        throw new IllegalArgumentException();
    }
}
