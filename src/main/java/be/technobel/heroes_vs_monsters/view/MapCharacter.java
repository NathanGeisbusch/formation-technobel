package be.technobel.heroes_vs_monsters.view;

enum MapCharacter {
    HIDDEN("?"),
    EMPTY("·"),
    HERO(ANSI.CYAN, "H", ANSI.RESET),
    LOUP(ANSI.YELLOW, "L", ANSI.RESET),
    ORC(ANSI.GREEN, "O", ANSI.RESET),
    DRAGON(ANSI.MAGENTA, "D", ANSI.RESET),
    HERO_MORT(ANSI.BG_RED, ANSI.BLUE, "H", ANSI.RESET),
    LOUP_MORT(ANSI.BG_RED, ANSI.YELLOW, "L", ANSI.RESET),
    ORC_MORT(ANSI.BG_RED, ANSI.GREEN, "O", ANSI.RESET),
    DRAGON_MORT(ANSI.BG_RED, ANSI.YELLOW, "D", ANSI.RESET);
    public final Object[] value;
    MapCharacter(Object... args) { this.value = args; }
}
