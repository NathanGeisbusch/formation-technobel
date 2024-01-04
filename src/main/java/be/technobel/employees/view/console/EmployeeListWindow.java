package be.technobel.employees.view.console;

import be.technobel.employees.db.ContractType;
import be.technobel.employees.db.EmployeeDatabase;
import be.technobel.employees.db.EmployeeSortBy;
import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class EmployeeListWindow extends BasicWindow implements CustomLayout {
    private final WindowBasedTextGUI textGUI;
    private final EmployeeDatabase db;
    private final EmployeeCreationWindow employeeCreationWindow;
    private final EmployeeUpdateWindow employeeUpdateWindow;
    private final Table<String> table;
    private final List<Map.Entry<String,ContractType>> choiceFilter;
    private final List<Map.Entry<String,EmployeeSortBy>> choiceSort;
    private final Panel panel;
    private Employee[] selection;
    private ContractType contractType;
    private EmployeeSortBy sortBy;
    private long page = 0;

    public EmployeeListWindow(WindowBasedTextGUI textGUI, EmployeeDatabase db) {
        super("Liste d'employés");
        assert textGUI != null : new NullPointerException("textGUI");
        assert db != null : new NullPointerException("db");
        this.textGUI = textGUI;
        this.db = db;
        this.employeeCreationWindow = new EmployeeCreationWindow(textGUI, db);
        this.employeeUpdateWindow = new EmployeeUpdateWindow(textGUI, db);
        this.table = new Table<>("Nom", "Date naissance", "Salaire", "Contrat");
        this.choiceFilter = List.of(
            Map.entry("Tous", ContractType.ALL),
            Map.entry("CDD", ContractType.FTC),
            Map.entry("CDI", ContractType.PC),
            Map.entry("Licencié", ContractType.FIRED),
            Map.entry("Non recruté", ContractType.NOT_RECRUITED)
        );
        this.choiceSort = List.of(
            Map.entry("↓ Nom", EmployeeSortBy.FULL_NAME_ASC),
            Map.entry("↑ Nom", EmployeeSortBy.FULL_NAME_DSC),
            Map.entry("↓ Prénom", EmployeeSortBy.FIRST_NAME_ASC),
            Map.entry("↑ Prénom", EmployeeSortBy.FIRST_NAME_DSC),
            Map.entry("↓ Salaire", EmployeeSortBy.SALARY_ASC),
            Map.entry("↑ Salaire", EmployeeSortBy.SALARY_DSC),
            Map.entry("↓ Date naissance", EmployeeSortBy.BIRTH_DATE_ASC),
            Map.entry("↑ Date naissance", EmployeeSortBy.BIRTH_DATE_DSC)
        );
        this.contractType = this.choiceFilter.get(0).getValue();
        this.sortBy = this.choiceSort.get(0).getValue();
        this.panel = this.initUI();
    }

    @Override
    public void close() {
        super.close();
        selection = null;
    }

    public EmployeeListWindow init() {
        setComponent(panel);
        this.page = 0;
        this.populateModel();
        return this;
    }

    private Panel initUI() {
        // Init components
        table.setPreferredSize(TABLE_SIZE_LARGE);
        table.setSelectAction(this::onSelect);
        var comboBoxFilter = new ComboBox<String>().addListener(this::onFilter);
        var comboBoxSort = new ComboBox<String>().addListener(this::onSort);
        for(var choice : choiceFilter) comboBoxFilter.addItem(choice.getKey());
        for(var choice : choiceSort) comboBoxSort.addItem(choice.getKey());
        // Layout
        return new Panel(GRID_LAYOUT_4)
            //critères de tri et filtre
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_4))
            .addComponent(new Label("Filtrer par"))
            .addComponent(comboBoxFilter)
            .addComponent(new Label("Trier par"))
            .addComponent(comboBoxSort)
            //tableau d'affichage
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_4))
            .addComponent(table.setLayoutData(FILL_4))
            //actions
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_4))
            .addComponent(PANEL_LINEAR_START
                .addComponent(new Button("Précédent", this::previousPage))
                .addComponent(new Button("Suivant", this::nextPage))
            )
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(PANEL_LINEAR_END
                .addComponent(new Button("Ajouter", this::add))
                .addComponent(new Button("Retour", this::close))
            );
    }

    private String contractTypeText(Employee employee) {
        if(employee.isFTC()) return "CDD";
        if(employee.isPC()) return "CDI";
        if(employee.isFired()) return "Licencié";
        return "";
    }

    private void onSelect() {
        if(selection == null || selection.length == 0) return;
        Employee employee = selection[table.getSelectedRow()];
        textGUI.addWindowAndWait(employeeUpdateWindow.init(employee));
        populateModel();
    }

    private void onFilter(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {
        contractType = choiceFilter.get(selectedIndex).getValue();
        populateModel();
    }

    private void onSort(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {
        sortBy = choiceSort.get(selectedIndex).getValue();
        populateModel();
    }

    private void previousPage() {
        if(page == 0) {
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Pas d'employés précédents", MessageDialogButton.OK);
        }
        else {
            --page;
            populateModel();
        }
    }

    private void nextPage() {
        if(db.get(contractType, (page*10)+10, 1, sortBy).length == 0) {
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Pas d'employés suivants", MessageDialogButton.OK);
        }
        else {
            ++page;
            populateModel();
        }
    }

    private void populateModel() {
        // Réinitialise le contenu du tableau d'affichage
        var model = table.getTableModel();
        model.clear();
        // Récupère 10 employés
        selection = db.get(contractType, page*10L, 10, sortBy);
        for(Employee row : selection) {
            String fullName = row.getFullName();
            model.addRow(
                fullName.length() > 24 ? fullName.substring(0, 22)+"..." : fullName,
                row.getBirthdate().toString(),
                row.getSalary().setScale(2, RoundingMode.FLOOR).toString(),
                contractTypeText(row)
            );
        }
    }

    private void add() {
        textGUI.addWindowAndWait(employeeCreationWindow.init());
        populateModel();
    }
}
