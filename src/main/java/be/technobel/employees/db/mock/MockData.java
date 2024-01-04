package be.technobel.employees.db.mock;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Générateur de données aléatoires */
public class MockData {
    private static MockData instance = null;
    private final List<String> firstNames = new ArrayList<>();
    private final List<String> lastNames = new ArrayList<>();

    private MockData() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputFirstNames = classLoader.getResourceAsStream("be/technobel/employees/first_names.txt");
        InputStream inputLastNames = classLoader.getResourceAsStream("be/technobel/employees/last_names.txt");
        if(inputFirstNames != null && inputLastNames != null) {
            try (
                BufferedReader readerFirstNames = new BufferedReader(new InputStreamReader(inputFirstNames));
                BufferedReader readerLastNames = new BufferedReader(new InputStreamReader(inputLastNames));
            ) {
                String line;
                while((line = readerFirstNames.readLine()) != null) firstNames.add(line);
                while((line = readerLastNames.readLine()) != null) lastNames.add(line);
            }
            catch(IOException ignored) {}
        }
    }

    /**
     * Initialise l'instance si elle n'a pas encore été créée, puis la renvoie.
     * @return Instance de l'objet
     */
    public static MockData getInstance() {
        if(MockData.instance == null) MockData.instance = new MockData();
        return MockData.instance;
    }

    /** @return Prénom aléatoire */
    public String firstName() {
        return !this.firstNames.isEmpty() ? this.firstNames.get(integer(0, this.firstNames.size())) : "";
    }

    /** @return Nom de famille aléatoire */
    public String lastName() {
        return !this.lastNames.isEmpty() ? this.lastNames.get(integer(0, this.lastNames.size())) : "";
    }

    /**
     * @param minYearInclusive Année minimum (inclusif)
     * @param maxYearExclusive Année maximum (exclusif)
     * @return Date aléatoire
     */
    public LocalDate dateBetween(int minYearInclusive, int maxYearExclusive) {
        assert minYearInclusive >= LocalDate.MIN.getYear() : new IllegalArgumentException("minYearInclusive >= LocalDate.MIN.getYear()");
        assert maxYearExclusive <= LocalDate.MAX.getYear() : new IllegalArgumentException("maxYearExclusive <= LocalDate.MAX.getYear()");
        assert minYearInclusive < maxYearExclusive : new IllegalArgumentException("minYearInclusive < maxYearExclusive");
        return LocalDate.of(integer(minYearInclusive, maxYearExclusive), integer(1, 13), integer(1, 29));
    }

    /**
     * @param minInclusive Nombre entier minimum (inclusif)
     * @param maxExclusive Nombre entier maximum (exclusif)
     * @return Nombre entier aléatoire
     */
    public int integer(int minInclusive, int maxExclusive) {
        assert minInclusive < maxExclusive : new IllegalArgumentException("minInclusive < maxExclusive");
        return ThreadLocalRandom.current().nextInt(minInclusive, maxExclusive);
    }
}