package be.technobel.employees.view.console;

import be.technobel.employees.db.EmployeeList;
import be.technobel.employees.view.AppUI;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.io.IOException;

/** Implémentation console de l'app. */
public class ConsoleAppUI extends AppUI {
    public ConsoleAppUI() {
        db = new EmployeeList();
    }

    @Override
    public void run() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setTerminalEmulatorTitle("Gestionnaire d'employés");
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            MainWindow mw = new MainWindow(textGUI, db);
            textGUI.addWindowAndWait(mw);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(screen != null) {
                try {
                    screen.stopScreen();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}