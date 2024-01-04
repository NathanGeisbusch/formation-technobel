package be.technobel.employees.view.console;

import be.technobel.employees.db.EmployeeDatabase;
import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EmployeeUpdateWindow extends BasicWindow implements CustomLayout {
    private final WindowBasedTextGUI textGUI;
    private final ContractListWindow contractListWindow;
    private final EmployeeDatabase db;
    private final TextBox inputLastName, inputFirstName, inputBirthDate, inputSalary;
    private final Label labelSalaryRaise;
    private final Panel panel;
    private Employee employee = null;

    public EmployeeUpdateWindow(WindowBasedTextGUI textGUI, EmployeeDatabase db) {
        super("Modification d'un employé");
        assert textGUI != null : new NullPointerException("textGUI");
        assert db != null : new NullPointerException("db");
        this.textGUI = textGUI;
        this.contractListWindow = new ContractListWindow(textGUI);
        this.inputLastName = new TextBox(TEXT_BOX_SIZE);
        this.inputFirstName = new TextBox(TEXT_BOX_SIZE);
        this.inputBirthDate = new TextBox(TEXT_BOX_SIZE).setValidationPattern(FieldRegex.DATE.value());
        this.inputSalary = new TextBox(TEXT_BOX_SIZE).setValidationPattern(FieldRegex.SALARY.value());
        this.inputSalary.setTextChangeListener(this::onSalaryChange);
        this.labelSalaryRaise = new Label("");
        this.db = db;
        this.panel = this.initUI();
    }

    @Override
    public void close() {
        super.close();
        employee = null;
    }

    public EmployeeUpdateWindow init(Employee employee) {
        assert employee != null : new NullPointerException("employee");
        setComponent(panel);
        this.employee = employee.clone();
        this.inputLastName.setText(employee.getLastName());
        this.inputFirstName.setText(employee.getFirstName());
        this.inputBirthDate.setText(employee.getBirthdate().toString());
        this.inputSalary.setText(employee.getSalary().setScale(2, RoundingMode.FLOOR).toString());
        this.labelSalaryRaise.setText(employee.getSalary().setScale(2, RoundingMode.FLOOR).toString());
        return this;
    }

    private Panel initUI() {
        return new Panel(GRID_LAYOUT_3)
            //lastName
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Nom de famille").setLayoutData(FILL_2))
            .addComponent(inputLastName)
            //firstName
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Prénom").setLayoutData(FILL_2))
            .addComponent(inputFirstName)
            //birthDate
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Date naissance").setLayoutData(FILL_2))
            .addComponent(inputBirthDate)
            //salary
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Salaire de base").setLayoutData(FILL_2))
            .addComponent(inputSalary)
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Salaire majoré").setLayoutData(FILL_2))
            .addComponent(labelSalaryRaise)
            //contrats
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Button("Contrats", this::contracts).setLayoutData(RIGHT))
            //actions
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_3))
            .addComponent(new Button("Supprimer", this::delete))
            .addComponent(new Button("Sauvegarder", this::save))
            .addComponent(new Button("Annuler", this::close).setLayoutData(RIGHT));
    }

    private void onSalaryChange(String newText, boolean changedByUserInteraction) {
        try {
            employee.setSalaryBase(new BigDecimal(newText));
            labelSalaryRaise.setText(employee.getSalary().setScale(2, RoundingMode.FLOOR).toString());
        }
        catch(NumberFormatException ignored) {}
    }

    private void contracts() {
        textGUI.addWindowAndWait(contractListWindow.init(employee));
        onSalaryChange(inputSalary.getText(), true);
    }

    private void delete() {
        MessageDialogButton ret = MessageDialog.showMessageDialog(textGUI, "Suppression d'un employé",
            "Êtes-vous certain de vouloir supprimer cet employé ?",
            MessageDialogButton.No, MessageDialogButton.Yes);
        if(ret == MessageDialogButton.Yes) {
            db.delete(employee);
            MessageDialog.showMessageDialog(textGUI, "Info", "Employé supprimé avec succès", MessageDialogButton.OK);
            this.close();
        }
    }

    private void save() {
        // Vérification si champs vides
        if(inputLastName.getText().isBlank())
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Le nom de famille ne doit pas être vide", MessageDialogButton.OK);
        else if(inputFirstName.getText().isBlank())
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Le prénom ne doit pas être vide", MessageDialogButton.OK);
        else if(inputBirthDate.getText().isBlank())
            MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de naissance ne doit pas être vide", MessageDialogButton.OK);
        else if(inputSalary.getText().isBlank())
            MessageDialog.showMessageDialog(textGUI, "Erreur", "Le salaire ne doit pas être vide", MessageDialogButton.OK);
        else {
            try {
                // Parse
                String lastName = inputLastName.getText();
                String firstName = inputFirstName.getText();
                LocalDate birthDate = LocalDate.parse(inputBirthDate.getText());
                BigDecimal salary = new BigDecimal(inputSalary.getText());
                // Modification de l'employé
                db.update(employee
                    .setLastName(lastName)
                    .setFirstName(firstName)
                    .setBirthdate(birthDate)
                    .setSalaryBase(salary));
                // Msg succès + close
                MessageDialog.showMessageDialog(textGUI, "Info", "Employé modifié avec succès", MessageDialogButton.OK);
                this.close();
            }
            catch(DateTimeParseException err) {
                MessageDialog.showMessageDialog(textGUI, "Erreur", "Date de naissance invalide", MessageDialogButton.OK);
            }
            catch(NumberFormatException err) {
                MessageDialog.showMessageDialog(textGUI, "Erreur", "Salaire invalide", MessageDialogButton.OK);
            }
        }
    }
}