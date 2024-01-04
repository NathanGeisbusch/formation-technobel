package be.technobel.employees.view.console;

import be.technobel.employees.model.Contract;
import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ContractCreationWindow extends BasicWindow implements CustomLayout {
    private final WindowBasedTextGUI textGUI;
    private final TextBox inputStartDate;
    private final TextBox inputEndDate;
    private final Panel panel;
    private Employee employee = null;

    public ContractCreationWindow(WindowBasedTextGUI textGUI) {
        super("Ajout d'un contrat");
        assert textGUI != null : new NullPointerException("textGUI");
        this.textGUI = textGUI;
        this.inputStartDate = new TextBox(TEXT_BOX_SIZE).setValidationPattern(FieldRegex.DATE.value());
        this.inputEndDate = new TextBox(TEXT_BOX_SIZE).setValidationPattern(FieldRegex.DATE.value());
        this.panel = this.initUI();
    }

    @Override
    public void close() {
        super.close();
        employee = null;
    }

    public ContractCreationWindow init(Employee employee) {
        assert employee != null : new NullPointerException("employee");
        setComponent(panel);
        this.employee = employee;
        this.inputStartDate.setText(LocalDate.now().toString());
        this.inputEndDate.setText("");
        return this;
    }

    private Panel initUI() {
        return new Panel(GRID_LAYOUT_2)
            // startDate
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Label("Date début"))
            .addComponent(inputStartDate)
            // endDate
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Label("Date fin"))
            .addComponent(inputEndDate)
            // actions
            .addComponent(new EmptySpace().setLayoutData(FILL_2))
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_2))
            .addComponent(new Button("Valider", this::save))
            .addComponent(new Button("Annuler", this::close).setLayoutData(RIGHT));
    }

    private void save() {
        // Vérification si champs vides
        if(inputStartDate.getText().isBlank())
            MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de début ne doit pas être vide", MessageDialogButton.OK);
        else {
            try {
                // Parse startDate + check si déjà utilisée
                LocalDate startDate = LocalDate.parse(inputStartDate.getText());
                if(employee.findContract(startDate).isPresent()) {
                    MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de début est déjà utilisée dans un autre contrat", MessageDialogButton.OK);
                    return;
                }
                // Parse endDate + check si startDate > endDate
                LocalDate endDate = inputEndDate.getText().isBlank() ? null : LocalDate.parse(inputEndDate.getText());
                if(endDate != null && startDate.isAfter(endDate)) {
                    MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de début ne peut pas être supérieure à la date de fin", MessageDialogButton.OK);
                    return;
                }
                // Check s'il reste de la place dans la liste de contrats
                if(employee.getContractsAmount() == Integer.MAX_VALUE) {
                    MessageDialog.showMessageDialog(textGUI, "Erreur", "Impossible d'ajouter un nouveau contrat", MessageDialogButton.OK);
                    return;
                }
                // Ajout du contrat
                employee.addContract(new Contract(startDate, endDate));
                // Msg succès + close
                MessageDialog.showMessageDialog(textGUI, "Info", "Contrat ajouté avec succès", MessageDialogButton.OK);
                this.close();
            }
            catch(DateTimeParseException err) {
                MessageDialog.showMessageDialog(textGUI, "Erreur", "Date invalide", MessageDialogButton.OK);
            }
        }
    }
}