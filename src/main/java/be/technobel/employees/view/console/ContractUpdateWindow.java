package be.technobel.employees.view.console;

import be.technobel.employees.model.Contract;
import be.technobel.employees.model.Employee;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ContractUpdateWindow extends BasicWindow implements CustomLayout {
    private final WindowBasedTextGUI textGUI;
    private final TextBox inputStartDate;
    private final TextBox inputEndDate;
    private final Panel panel;
    private Employee employee = null;
    private Contract contract = null;

    public ContractUpdateWindow(WindowBasedTextGUI textGUI) {
        super("Modification d'un contrat");
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
        contract = null;
    }

    public ContractUpdateWindow init(Employee employee, Contract contract) {
        assert employee != null : new NullPointerException("employee");
        assert contract != null : new NullPointerException("contract");
        setComponent(panel);
        this.employee = employee;
        this.contract = contract;
        inputStartDate.setText(contract.getStartDate().toString());
        inputEndDate.setText(contract.getEndDate().map(LocalDate::toString).orElse(""));
        return this;
    }

    private Panel initUI() {
        return new Panel(GRID_LAYOUT_3)
            // startDate
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Date début").setLayoutData(FILL_2))
            .addComponent(inputStartDate)
            // endDate
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Label("Date fin").setLayoutData(FILL_2))
            .addComponent(inputEndDate)
            // actions
            .addComponent(new EmptySpace().setLayoutData(FILL_3))
            .addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(FILL_3))
            .addComponent(new Button("Supprimer", this::delete))
            .addComponent(new Button("Valider", this::save))
            .addComponent(new Button("Annuler", this::close).setLayoutData(RIGHT));
    }

    private void delete() {
        MessageDialogButton ret = MessageDialog.showMessageDialog(textGUI, "Suppression d'un contrat",
            "Êtes-vous certain de vouloir supprimer ce contrat ?",
            MessageDialogButton.No, MessageDialogButton.Yes);
        if(ret == MessageDialogButton.Yes) {
            employee.removeContract(contract);
            MessageDialog.showMessageDialog(textGUI, "Info", "Contrat supprimé avec succès", MessageDialogButton.OK);
            this.close();
        }
    }

    private void save() {
        // Vérification si champs vides
        if(inputStartDate.getText().isBlank())
            MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de début ne doit pas être vide", MessageDialogButton.OK);
        else {
            try {
                // Parse startDate + check si déjà utilisée
                LocalDate startDate = LocalDate.parse(inputStartDate.getText());
                if(employee.findContract(startDate).filter(x -> x != contract).isPresent()) {
                    MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de début est déjà utilisée dans un autre contrat", MessageDialogButton.OK);
                    return;
                }
                // Parse endDate + check si startDate > endDate
                LocalDate endDate = inputEndDate.getText().isBlank() ? null : LocalDate.parse(inputEndDate.getText());
                if(endDate != null && startDate.isAfter(endDate)) {
                    MessageDialog.showMessageDialog(textGUI, "Erreur", "La date de début ne peut pas être supérieure à la date de fin", MessageDialogButton.OK);
                    return;
                }
                // Modification du contrat
                contract.setStartDate(startDate).setEndDate(endDate);
                // Msg succès + close
                MessageDialog.showMessageDialog(textGUI, "Info", "Contrat modifié avec succès", MessageDialogButton.OK);
                this.close();
            } catch(DateTimeParseException err) {
                MessageDialog.showMessageDialog(textGUI, "Erreur", "Date invalide", MessageDialogButton.OK);
            }
        }
    }
}