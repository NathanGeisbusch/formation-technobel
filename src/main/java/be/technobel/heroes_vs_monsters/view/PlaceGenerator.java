package be.technobel.heroes_vs_monsters.view;

import be.technobel.heroes_vs_monsters.model.*;

public abstract class PlaceGenerator {
    public static Hero generate(Personnage[][] map, int amountCHR, Game.HeroConstructor heroConstructor) {
        Hero result = null;
        int remaining = Math.max(amountCHR, 0);
        boolean heroNonPlace = true;
        //Placement random
        for(int y = 0; y < map.length && remaining > 0; y++) {
            for(int x = 0; x < map[y].length && remaining > 0; x++) {
                if (okWest(map, x, y) && okNorth(map, x, y) && okNorthWest(map, x, y) && okNorthEast(map, x, y) && De.DE12.lance() == 1) {
                    Hero heroTmp = createCHR(map, x, y, remaining, heroNonPlace, heroConstructor);
                    if(heroTmp != null) {
                        result = heroTmp;
                        heroNonPlace = false;
                    }
                    --remaining;
                }
            }
        }
        //Placement restant sans random
        for(int y = 0; y < map.length && remaining > 0; y++) {
            for(int x = 0; x < map[y].length && remaining > 0; x++) {
                if (okWest(map, x, y) && okNorth(map, x, y) && okNorthWest(map, x, y) && okNorthEast(map, x, y) &&
                        okEast(map, x, y) && okSouthWest(map, x, y) && okSouth(map, x, y) && okSouthEast(map, x, y)) {
                    Hero heroTmp = createCHR(map, x, y, remaining, heroNonPlace, heroConstructor);
                    if(heroTmp != null) {
                        result = heroTmp;
                        heroNonPlace = false;
                    }
                    --remaining;
                }
            }
        }
        return result;
    }
    private static boolean okNorth(Personnage[][] map, int x, int y) {
        if(y == 0) return true;
        if(y == 1) return map[y-1][x] == null;
        return map[y-1][x] == null && map[y-2][x] == null;
    }
    private static boolean okSouth(Personnage[][] map, int x, int y) {
        if(y == map.length-1) return true;
        if(y == map.length-2) return map[y+1][x] == null;
        return map[y+1][x] == null && map[y+2][x] == null;
    }
    private static boolean okWest(Personnage[][] map, int x, int y) {
        if(x == 0) return true;
        if(x == 1) return map[y][x-1] == null;
        return map[y][x-1] == null && map[y][x-2] == null;
    }
    private static boolean okEast(Personnage[][] map, int x, int y) {
        if(x == map.length-1) return true;
        if(x == map.length-2) return map[y][x+1] == null;
        return map[y][x+1] == null && map[y][x+2] == null;
    }
    private static boolean okNorthWest(Personnage[][] map, int x, int y) {
        if(x == 0 || y == 0) return true;
        if(x == 1 || y == 1) return map[y-1][x-1] == null;
        return map[y-1][x-1] == null && map[y-2][x-2] == null;
    }
    private static boolean okNorthEast(Personnage[][] map, int x, int y) {
        if(x == map.length-1 || y == 0) return true;
        if(x == map.length-2 || y == 1) return map[y-1][x+1] == null;
        return map[y-1][x+1] == null && map[y-2][x+2] == null;
    }
    private static boolean okSouthEast(Personnage[][] map, int x, int y) {
        if(x == map.length-1 || y == map.length-1) return true;
        if(x == map.length-2 || y == map.length-2) return map[y+1][x+1] == null;
        return map[y+1][x+1] == null && map[y+2][x+2] == null;
    }
    private static boolean okSouthWest(Personnage[][] map, int x, int y) {
        if(x == 0 || y == map.length-1) return true;
        if(x == 1 || y == map.length-2) return map[y+1][x-1] == null;
        return map[y+1][x-1] == null && map[y+2][x-2] == null;
    }
    private static Hero createCHR(Personnage[][] map, int x, int y, int remaining, boolean heroNonPlace,
                                  Game.HeroConstructor heroConstructor) {
        Hero result = null;
        //Placement du hero
        if(heroNonPlace && (remaining == 1 || De.DE4.lance() == 1)) {
            map[y][x] = result = heroConstructor.constructor(x,y);
        }
        //Placement d'un monstre
        else {
            map[y][x] = switch(De.DE3.lance()) {
                case 1 -> new Loup(x,y);
                case 2 -> new Orc(x,y);
                case 3 -> new Dragon(x,y);
                default -> null;
            };
        }
        return result;
    }
}
