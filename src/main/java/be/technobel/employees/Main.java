package be.technobel.employees;

import be.technobel.employees.view.AppUI;
import be.technobel.employees.view.console.ConsoleAppUI;

public class Main {
    public static void main(String[] args) {
        AppUI app = new ConsoleAppUI();
        app.run();
    }
}
