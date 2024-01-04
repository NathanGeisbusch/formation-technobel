package be.technobel.employees.view;

import be.technobel.employees.db.EmployeeDatabase;

public abstract class AppUI {
    protected EmployeeDatabase db;

    /** Initialise et démarre l'interface graphique. */
    public abstract void run();
}
