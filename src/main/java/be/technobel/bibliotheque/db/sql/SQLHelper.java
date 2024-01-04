package be.technobel.bibliotheque.db.sql;

public class SQLHelper {
    /** like ? escape '!' */
    private static String escapeLikePattern(String search) {
        return search
            .replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_")
            .replace("[", "![");
    }
    public static String escapeLikePatternPrefix(String search) {
        return escapeLikePattern(search)+"%";
    }
    public static String escapeLikePatternSuffix(String search) {
        return "%"+ escapeLikePattern(search);
    }
    public static String escapeLikePatternGlobal(String search) {
        return "%"+ escapeLikePattern(search)+"%";
    }
}
