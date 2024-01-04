package be.technobel.employees.view.console;

import be.technobel.employees.db.ContractType;
import be.technobel.employees.db.EmployeeDatabase;
import be.technobel.employees.db.mock.MockData;
import be.technobel.employees.model.Contract;
import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import java.math.RoundingMode;

public class MainWindow extends BasicWindow {
    private final WindowBasedTextGUI textGUI;
    private final EmployeeDatabase db;
    private final EmployeeCreationWindow employeeCreationWindow;
    private final EmployeeListWindow employeeListWindow;

    public MainWindow(WindowBasedTextGUI textGUI, EmployeeDatabase db) {
        super("Gestionnaire d'employés");
        assert textGUI != null : new NullPointerException("textGUI");
        assert db != null : new NullPointerException("db");
        this.textGUI = textGUI;
        this.db = db;
        this.employeeCreationWindow = new EmployeeCreationWindow(textGUI, db);
        this.employeeListWindow = new EmployeeListWindow(textGUI, db);
        initUI();
    }

    private void initUI() {
        setComponent(new MenuBar()
            .add(new Menu("Employés")
                .add(new MenuItem("Ajouter", () -> textGUI.addWindowAndWait(employeeCreationWindow.init())))
                .add(new MenuItem("Liste", () -> textGUI.addWindowAndWait(employeeListWindow.init())))
                .add(new MenuItem("Moyenne salaires", this::showAvgSalary))
                .add(new MenuItem("Total salaires", this::showTotalSalary))
            )
            .add(new Menu("Options")
                .add(new MenuItem("Vider la base de données", this::clearDB))
                .add(new MenuItem("Remplir la base de données", this::fillDB))
                .add(new MenuItem("Quitter", this::close))
            )
            .add(new Menu("Aide")
                .add(new MenuItem("À propos", this::showAbout))
            )
        );
    }

    private void showAvgSalary() {
        MessageDialog.showMessageDialog(textGUI, "Moyenne salaires",
            String.format("\n%s €", db.getAvgSalary().setScale(2, RoundingMode.FLOOR)),
            MessageDialogButton.OK);
    }

    private void showTotalSalary() {
        MessageDialog.showMessageDialog(textGUI, "Total salaires",
            String.format("\n%s €", db.getTotalSalary().setScale(2, RoundingMode.FLOOR)),
            MessageDialogButton.OK);
    }

    private void showAbout() {
        MessageDialog.showMessageDialog(textGUI, "À propos",
        "\nProduction Java Technobel\nDéveloppé par Nathan Geisbusch",
            MessageDialogButton.OK);
    }

    private void clearDB() {
        Employee[] all = db.get(ContractType.ALL, 0, 0);
        for(var e : all) db.delete(e);
        MessageDialog.showMessageDialog(textGUI, "Base de données",
            "\nBase de données vidée avec succès",
            MessageDialogButton.OK);
    }

    private void fillDB() {
        MockData mock = MockData.getInstance();
        for(int i = 0; i < 32; i++) {
            Contract contract = mock.integer(0,2) == 0 ?
                new Contract(mock.dateBetween(2010, 2023)) :
                new Contract(mock.dateBetween(2010, 2023), mock.dateBetween(2024, 2026));
            db.add(new Employee()
                .setLastName(mock.lastName())
                .setFirstName(mock.firstName())
                .setBirthdate(mock.dateBetween(1950, 2000))
                .setSalaryBase(mock.integer(1500, 2500))
                .addContract(contract));
        }
        MessageDialog.showMessageDialog(textGUI, "Base de données",
            "\nBase de données remplie avec succès",
            MessageDialogButton.OK);
    }
}
