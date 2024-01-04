package be.technobel.employees.view.console;

import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import java.time.LocalDate;

public class ContractListWindow extends BasicWindow implements CustomLayout {
    private final WindowBasedTextGUI textGUI;
    private final ContractCreationWindow contractCreationWindow;
    private final ContractUpdateWindow contractUpdateWindow;
    private final Table<String> table;
    private final Panel panel;
    private Employee employee = null;
    private long page = 0;

    public ContractListWindow(WindowBasedTextGUI textGUI) {
        super("Liste de contrats");
        assert textGUI != null : new NullPointerException("textGUI");
        this.textGUI = textGUI;
        this.contractCreationWindow = new ContractCreationWindow(textGUI);
        this.contractUpdateWindow = new ContractUpdateWindow(textGUI);
        this.table = new Table<>("Date début", "Date fin");
        this.panel = this.initUI();
    }

    @Override
    public void close() {
        super.close();
        employee = null;
    }

    public ContractListWindow init(Employee employee) {
        assert employee != null : new NullPointerException("employee");
        setComponent(panel);
        this.employee = employee;
        this.page = 0;
        this.populateModel();
        return this;
    }

    private Panel initUI() {
        table.setPreferredSize(TABLE_SIZE);
        table.setSelectAction(this::onSelect);
        return new Panel(GRID_LAYOUT_4)
            //tableau d'affichage
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_4))
            .addComponent(table.setLayoutData(FILL_4))
            //actions
            .addComponent(new EmptySpace().setLayoutData(FILL_4))
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_4))
            .addComponent(new Button("Précédent", this::previousPage))
            .addComponent(new Button("Suivant", this::nextPage))
            .addComponent(new Button("Ajouter", this::add).setLayoutData(RIGHT))
            .addComponent(new Button("Retour", this::close).setLayoutData(RIGHT));
    }

    private void populateModel() {
        // Réinitialise le contenu du tableau d'affichage
        var model = table.getTableModel();
        model.clear();
        // Récupère 10 contrats
        employee.getContracts().skip(page*10).limit(10).forEach(c -> model.addRow(
            c.getStartDate().toString(),
            c.getEndDate().map(LocalDate::toString).orElse("")
        ));
    }

    private void onSelect() {
        // Récupère la date de début dans la ligne sélectionnée
        var model = table.getTableModel();
        if(model.getRowCount() == 0) return;
        String startDate = model.getRow(table.getSelectedRow()).get(0);
        // Trouve le contrat avec la même date, et affiche la fenêtre de modification de contrat
        textGUI.addWindowAndWait(contractUpdateWindow.init(employee, employee.getContracts()
            .filter(c -> c.getStartDate().toString().equals(startDate))
            .findFirst().orElseThrow()
        ));
        // Met à jour le contenu du tableau d'affichage (pour prendre en compte la modification du contrat)
        populateModel();
    }

    private void previousPage() {
        if(page == 0) {
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Pas de contrats précédents", MessageDialogButton.OK);
        }
        else {
            --page;
            populateModel();
        }
    }

    private void nextPage() {
        if((page*10)+10 >= employee.getContractsAmount()) {
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Pas de contrats suivants", MessageDialogButton.OK);
        }
        else {
            ++page;
            populateModel();
        }
    }

    private void add() {
        textGUI.addWindowAndWait(contractCreationWindow.init(employee));
        populateModel();
    }
}