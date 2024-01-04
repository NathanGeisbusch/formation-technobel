package be.technobel.aquarium.model.mock;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Générateur de données aléatoires */
public class MockData {
    private static MockData instance = null;
    private final List<String> firstNames = new ArrayList<>();

    public MockData() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputFirstNames = classLoader.getResourceAsStream("assets/first_names.txt");
        if(inputFirstNames != null) {
            try (BufferedReader readerFirstNames = new BufferedReader(new InputStreamReader(inputFirstNames))) {
                String line;
                while((line = readerFirstNames.readLine()) != null) firstNames.add(line);
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