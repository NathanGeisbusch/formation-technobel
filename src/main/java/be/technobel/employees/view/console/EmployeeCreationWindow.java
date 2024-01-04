package be.technobel.employees.view.console;

import be.technobel.employees.db.EmployeeDatabase;
import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EmployeeCreationWindow extends BasicWindow implements CustomLayout {
    private final WindowBasedTextGUI textGUI;
    private final ContractListWindow contractListWindow;
    private final EmployeeDatabase db;
    private final TextBox inputLastName, inputFirstName, inputBirthDate, inputSalary;
    private final Panel panel;
    private Employee employee = null;

    public EmployeeCreationWindow(WindowBasedTextGUI textGUI, EmployeeDatabase db) {
        super("Ajout d'un employé");
        assert textGUI != null : new NullPointerException("textGUI");
        assert db != null : new NullPointerException("db");
        this.textGUI = textGUI;
        this.contractListWindow = new ContractListWindow(textGUI);
        this.inputLastName = new TextBox(TEXT_BOX_SIZE);
        this.inputFirstName = new TextBox(TEXT_BOX_SIZE);
        this.inputBirthDate = new TextBox(TEXT_BOX_SIZE).setValidationPattern(FieldRegex.DATE.value());
        this.inputSalary = new TextBox(TEXT_BOX_SIZE).setValidationPattern(FieldRegex.SALARY.value());
        this.db = db;
        this.panel = this.initUI();
    }

    @Override
    public void close() {
        super.close();
        employee = null;
    }

    public EmployeeCreationWindow init() {
        setComponent(panel);
        this.employee = new Employee();
        this.inputLastName.setText("");
        this.inputFirstName.setText("");
        this.inputBirthDate.setText(LocalDate.now().toString());
        this.inputSalary.setText("0");
        return this;
    }

    private Panel initUI() {
        return new Panel(GRID_LAYOUT_2)
            //lastName
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Label("Nom de famille"))
            .addComponent(inputLastName)
            //firstName
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Label("Prénom"))
            .addComponent(inputFirstName)
            //birthDate
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Label("Date naissance"))
            .addComponent(inputBirthDate)
            //salary
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Label("Salaire de base"))
            .addComponent(inputSalary)
            //contrats
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new EmptySpace())
            .addComponent(new Button("Contrats", this::contracts).setLayoutData(RIGHT))
            //actions
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_2))
            .addComponent(new Button("Sauvegarder", this::save))
            .addComponent(new Button("Annuler", this::close).setLayoutData(RIGHT));
    }

    private void contracts() {
        textGUI.addWindowAndWait(contractListWindow.init(employee));
    }

    private void save() {
        // Vérifie si champs vides
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
                // Ajout de l'employé + check
                boolean ok = db.add(employee
                    .setLastName(lastName)
                    .setFirstName(firstName)
                    .setBirthdate(birthDate)
                    .setSalaryBase(salary));
                if(!ok) {
                    MessageDialog.showMessageDialog(textGUI, "Erreur", "Impossible d'ajouter un nouvel employé", MessageDialogButton.OK);
                    return;
                }
                // Msg succès + close
                MessageDialog.showMessageDialog(textGUI, "Info", "Employé ajouté avec succès", MessageDialogButton.OK);
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