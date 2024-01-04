package be.technobel.heroes_vs_monsters.view;

import be.technobel.heroes_vs_monsters.model.*;
import java.util.Arrays;

public class Game {
    @FunctionalInterface
    protected interface HeroConstructor {
        Hero constructor(int x, int y);
    }

    private final Console CONSOLE = new Console("Heroes vs Monsters");
    private final StringBuilder STRING_BUILDER = new StringBuilder();
    private final Object[] MOVE_PROMPT;
    private final Object[] MOVE_ERR_OOB;
    private final Object[] MOVE_ERR_MONSTER;
    private final int MAP_WIDTH = 15;
    private final int MONSTERS_AMOUNT = 10;
    private final boolean[][] mapExplored = new boolean[MAP_WIDTH][MAP_WIDTH];
    private final Personnage[][] map = new Personnage[MAP_WIDTH][MAP_WIDTH];
    private int exploredCounter;
    private Hero hero;
    private Monstre ennemi;
    private int remainingMonsters;

    public Game() {
        this.MOVE_PROMPT = new Object[]{"Dans quelle ", ANSI.BOLD, "direction", ANSI.RESET, " souhaitez-vous aller ?", ANSI.LINE_BREAK};
        this.MOVE_ERR_OOB = new Object[]{"Vous ne pouvez pas sortir de la forêt de ", ANSI.ITALIC, "Shorewood", ANSI.RESET, " !", ANSI.LINE_BREAK};
        this.MOVE_ERR_MONSTER = new Object[]{"Ce monstre est mort ! Laissez-le tranquille !", ANSI.LINE_BREAK};
    }

    private void stop() {
        CONSOLE.close();
        System.exit(0);
    }
    public void run() {
        //intro
        CONSOLE.init();
        CONSOLE.clear();
        CONSOLE.print(ANSI.LINE_BREAK, ANSI.UNDERLINE, "Heroes vs Monsters", ANSI.UNDERLINE_OFF, ANSI.LINE_BREAK,
                "Bienvenue dans la forêt de ", ANSI.ITALIC, "Shorewood", ANSI.ITALIC_OFF,
                ", forêt enchantée du pays de ", ANSI.ITALIC, "Stormwall", ANSI.ITALIC_OFF, ".", ANSI.LINE_BREAK,
                "Dans cette forêt, se livre un combat acharné entre les ", ANSI.CYAN, "héros",
                ANSI.RESET, " d'une part", ANSI.LINE_BREAK, "et les ", ANSI.RED, "monstres", ANSI.RESET,
                " d'autre part.", ANSI.LINE_BREAK, ANSI.LINE_BREAK);

        //choix hero
        CONSOLE.print("Souhaitez-vous incarner un (", ANSI.BOLD, "H", ANSI.BOLD_OFF,
                ")umain (", ANSI.ITALIC, "+1 STR, +1 END", ANSI.ITALIC_OFF, ") ou un (",
                ANSI.BOLD, "N", ANSI.BOLD_OFF, ")ain (", ANSI.ITALIC,
                "+2 END", ANSI.ITALIC_OFF, ") ?", ANSI.LINE_BREAK);
        char c;
        do {
            c = Character.toUpperCase(CONSOLE.getChar());
            if(c == 'X') stop();
        } while(c != 'N' && c != 'H');

        //init
        this.hero = PlaceGenerator.generate(map, MONSTERS_AMOUNT+1, c == 'H' ? Humain::new : Nain::new);
        for(boolean[] booleans : mapExplored) Arrays.fill(booleans, false);
        this.exploredCounter = 0;
        this.ennemi = null;
        this.remainingMonsters = MONSTERS_AMOUNT;

        //jeu
        this.explore();
        do {
            this.displayMap();
            this.move();
            this.explore();
            if(this.remainingMonsters == 0) {
                CONSOLE.print("Félicitations ! Vous avez vaincu tous les monstres de la forêt de ",
                        ANSI.ITALIC, "Shorewood", ANSI.ITALIC_OFF, " !", ANSI.LINE_BREAK);
                break;
            }
            if(this.hero.estMort()) {
                CONSOLE.print("Vous avez été vaincu. Game Over...", ANSI.LINE_BREAK);
                break;
            }
        } while(true);
        CONSOLE.getChar();
        CONSOLE.close();
    }

    //Déplacement
    private void move() {
        final int x = hero.getX(), y = hero.getY();
        CONSOLE.print(MOVE_PROMPT);
        char c;
        boolean validInput;
        do {
            c = Character.toUpperCase(CONSOLE.getChar());
            if(c == 'X') stop();
            validInput = switch(c) {
                case 'N', 'Z', '↑' -> {
                    if(y == 0) {
                        CONSOLE.print(MOVE_ERR_OOB);
                        yield false;
                    }
                    if(this.map[y-1][x] != null) {
                        CONSOLE.print(MOVE_ERR_MONSTER);
                        yield false;
                    }
                    this.hero.setXY(x, y-1);
                    this.map[y-1][x] = this.hero;
                    this.map[y][x] = null;
                    yield true;
                }
                case 'O', 'Q', '←' -> {
                    if(x == 0) {
                        CONSOLE.print(MOVE_ERR_OOB);
                        yield false;
                    }
                    if(this.map[y][x-1] != null) {
                        CONSOLE.print(MOVE_ERR_MONSTER);
                        yield false;
                    }
                    this.hero.setXY(x-1, y);
                    this.map[y][x-1] = this.hero;
                    this.map[y][x] = null;
                    yield true;
                }
                case 'S', '↓' -> {
                    if(y == MAP_WIDTH-1) {
                        CONSOLE.print(MOVE_ERR_OOB);
                        yield false;
                    }
                    if(this.map[y+1][x] != null) {
                        CONSOLE.print(MOVE_ERR_MONSTER);
                        yield false;
                    }
                    this.hero.setXY(x, y+1);
                    this.map[y+1][x] = this.hero;
                    this.map[y][x] = null;
                    yield true;
                }
                case 'E', 'D', '→' -> {
                    if(x == MAP_WIDTH-1) {
                        CONSOLE.print(MOVE_ERR_OOB);
                        yield false;
                    }
                    if(this.map[y][x+1] != null) {
                        CONSOLE.print(MOVE_ERR_MONSTER);
                        yield false;
                    }
                    this.hero.setXY(x+1, y);
                    this.map[y][x+1] = this.hero;
                    this.map[y][x] = null;
                    yield true;
                }
                default -> false;
            };
        } while(!validInput);
    }

    private void explore() {
        final int x = hero.getX(), y = hero.getY();
        //CASE ACTUELLE
        if(!mapExplored[y][x]) {
            mapExplored[y][x] = true;
            ++exploredCounter;
        }
        //NORD
        if(y != 0 && !mapExplored[y-1][x]) {
            mapExplored[y-1][x] = true;
            ++exploredCounter;
            Monstre m = (Monstre) this.map[y-1][x];
            if(m != null) this.battle(m);
        }
        //EST
        if(x != MAP_WIDTH-1 && !mapExplored[y][x+1]) {
            mapExplored[y][x+1] = true;
            ++exploredCounter;
            Monstre m = (Monstre) this.map[y][x+1];
            if(m != null) this.battle(m);
        }
        //SUD
        if(y != MAP_WIDTH-1 && !mapExplored[y+1][x]) {
            mapExplored[y+1][x] = true;
            ++exploredCounter;
            Monstre m = (Monstre) this.map[y+1][x];
            if(m != null) this.battle(m);
        }
        //OUEST
        if(x != 0 && !mapExplored[y][x-1]) {
            mapExplored[y][x-1] = true;
            ++exploredCounter;
            Monstre m = (Monstre) this.map[y][x-1];
            if(m != null) this.battle(m);
        }
    }

    private void battle(Monstre m) {
        this.ennemi = m;
        this.displayMap();
        CONSOLE.print("Un monstre sauvage apparaît.", ANSI.LINE_BREAK);
        int dmg;
        do {
            STRING_BUILDER.setLength(0);
            dmg = this.hero.frappe(m);
            STRING_BUILDER.append(String.format("Vous attaquez %s (%d dégâts).\n", m, dmg));
            if(!m.estMort()) {
                dmg = m.frappe(hero);
                STRING_BUILDER.append(String.format("%s vous attaque (%d dégâts).\n", m, dmg));
            }
            else STRING_BUILDER.append(String.format("Vous avez vaincu %s.\n", m));
            String log = STRING_BUILDER.toString();
            this.displayMap();
            CONSOLE.print(log);
            if(Character.toUpperCase(CONSOLE.getChar()) == 'X') stop();
        } while(!m.estMort() && !this.hero.estMort());
        if(m.estMort()) {
            hero.restaurerPv();
            hero.addOr(m.getOr());
            hero.addCuir(m.getCuir());
            this.ennemi = null;
            --remainingMonsters;
        }
    }

    private void displayMap() {
        CONSOLE.clear();
        final int NB_LINES = MAP_WIDTH+2;
        for(int line = 0; line < NB_LINES; line++) {
            generateMap(line);
            generateStats(line);
            generateHelp(line);
        }
        CONSOLE.show();
    }
    private void generateMap(int line) {
        if(line == 0) CONSOLE.append('┏'+("━".repeat(1+MAP_WIDTH*2)));
        else if(line == MAP_WIDTH+1) CONSOLE.append('┗'+("━".repeat(1+MAP_WIDTH*2)));
        else if(line > 0 && line < MAP_WIDTH+1) {
            Monstre m;
            int y = line-1;
            CONSOLE.append('┃');
            for(int x = 0; x < MAP_WIDTH; x++) {
                CONSOLE.append(" ");
                //Si hero
                if(this.hero.getX() == x && this.hero.getY() == y) {
                    if (this.hero.estMort()) CONSOLE.append(MapCharacter.HERO_MORT.value);
                    else CONSOLE.append(MapCharacter.HERO.value);
                }
                //Si inexploré
                else if(!this.mapExplored[y][x]) CONSOLE.append(MapCharacter.HIDDEN.value);
                //Si monstre
                else if((m = (Monstre)this.map[y][x]) != null) {
                    switch(m.toString()) {
                        case "Loup" -> {
                            if(m.estMort()) CONSOLE.append(MapCharacter.LOUP_MORT.value);
                            else CONSOLE.append(MapCharacter.LOUP.value);
                        }
                        case "Orc" -> {
                            if(m.estMort()) CONSOLE.append(MapCharacter.ORC_MORT.value);
                            else CONSOLE.append(MapCharacter.ORC.value);
                        }
                        case "Dragon" -> {
                            if(m.estMort()) CONSOLE.append(MapCharacter.DRAGON_MORT.value);
                            else CONSOLE.append(MapCharacter.DRAGON.value);
                        }
                    }
                }
                //Si vide
                else CONSOLE.append(MapCharacter.EMPTY.value);
            }
            CONSOLE.append(' ');
        }
    }
    private void generateStats(int line) {
        int MAP_SIZE = MAP_WIDTH * MAP_WIDTH;
        switch(line) {
            case  0 -> CONSOLE.append(              "┳━━━━━━━━━━━━━━━━━━━━━━━━");
            case  1 -> CONSOLE.append(              "┃   ", ANSI.BOLD, "Heroes vs Monsters", ANSI.RESET, "   ");
            case  2, 8, 12 -> CONSOLE.append(       "┣━━━━━━━━━━━━━━━━━━━━━━━━");
            case  3 -> CONSOLE.append(String.format("┃ Hero: %-17s", this.hero.toString()));
            case  4 -> CONSOLE.append(String.format("┃ Or:   %-17s", this.hero.getOr()));
            case  5 -> CONSOLE.append(String.format("┃ Cuir: %-17s", this.hero.getCuir()));
            case  6 -> CONSOLE.append(String.format("┃ PV=%02d/%02d END=%-2s FOR=%-2s ",
                    this.hero.getPvRestants(), this.hero.getPv(), this.hero.getEndurance(), this.hero.getForce()));
            case  7, 11, 15 -> CONSOLE.append(      "┃                        ");
            case  9 -> CONSOLE.append(String.format("┃ Monstre: %-14s", this.ennemi == null ? "" : this.ennemi));
            case 10 -> CONSOLE.append(this.ennemi != null ?
                    String.format("┃ PV=%02d/%02d END=%-2s FOR=%-2s ",
                    this.ennemi.getPvRestants(), this.ennemi.getPv(), this.ennemi.getEndurance(), this.ennemi.getForce()) :
                    "┃                        ");
            case 13 -> CONSOLE.append(String.format("┃ Carte explorée à %-6s", ((this.exploredCounter*100)/ MAP_SIZE)+" %"));
            case 14 -> CONSOLE.append(String.format("┃ %-2s monstres restants   ", this.remainingMonsters));
            case 16 -> CONSOLE.append(              "┻━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    private void generateHelp(int line) {
        switch(line) {
            case  0 -> CONSOLE.append(              "┳━━━━━━━━━━━━━━━━━━━━━┓", ANSI.LINE_BREAK);
            case  1 -> CONSOLE.append(              "┃        Aide         ┃", ANSI.LINE_BREAK);
            case  2 -> CONSOLE.append(              "╋━━━━━━━━━━━━━━━━━━━━━┫", ANSI.LINE_BREAK);
            case  3 -> CONSOLE.append(              "┃ Directions:         ┃", ANSI.LINE_BREAK);
            case  4 -> CONSOLE.append(              "┃ ", ANSI.BOLD,"N", ANSI.BOLD_OFF, "ord ", ANSI.BOLD, "E", ANSI.BOLD_OFF,
                    "st ", ANSI.BOLD, "S", ANSI.BOLD_OFF, "ud ", ANSI.BOLD,"O", ANSI.BOLD_OFF, "uest  ┃", ANSI.LINE_BREAK);
            case  5, 15 -> CONSOLE.append(          "┃                     ┃", ANSI.LINE_BREAK);
            case  6 -> CONSOLE.append(              "┣━━━━━━━━━━━━━━━━━━━━━┫", ANSI.LINE_BREAK);
            case  7 -> CONSOLE.append(              "┃ Carte:              ┃", ANSI.LINE_BREAK);
            case  8 -> CONSOLE.append(              "┫ ? = Inexploré       ┃", ANSI.LINE_BREAK);
            case  9 -> CONSOLE.append(              "┃ · = Exploré         ┃", ANSI.LINE_BREAK);
            case 10 -> CONSOLE.append(              "┃ ").append(MapCharacter.HERO.value).append(" = Hero            ┃", ANSI.LINE_BREAK);
            case 11 -> CONSOLE.append(              "┃ ").append(MapCharacter.LOUP.value).append(" = Loup            ┃", ANSI.LINE_BREAK);
            case 12 -> CONSOLE.append(              "┫ ").append(MapCharacter.ORC.value).append(" = Orc             ┃", ANSI.LINE_BREAK);
            case 13 -> CONSOLE.append(              "┃ ").append(MapCharacter.DRAGON.value).append(" = Dragon          ┃", ANSI.LINE_BREAK);
            case 14 -> CONSOLE.append(              "┃ ").append(MapCharacter.DRAGON_MORT.value).append(" = Mort            ┃", ANSI.LINE_BREAK);
            case 16 -> CONSOLE.append(              "┻━━━━━━━━━━━━━━━━━━━━━┛", ANSI.LINE_BREAK);

        }
    }
}
