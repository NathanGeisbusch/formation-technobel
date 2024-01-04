package be.technobel.bibliotheque.db;

public final class DBInstance {
    private static Database instance = null;
    private DBInstance() {}
    public static void init(Database instance) throws Database.DatabaseException {
        if(DBInstance.instance != null) DBInstance.instance.close();
        DBInstance.instance = instance;
    }
    public static void close() throws Database.DatabaseException {
        if(DBInstance.instance != null) DBInstance.instance.close();
        DBInstance.instance = null;
    }
    public static Database get() {
        return instance;
    }
}
