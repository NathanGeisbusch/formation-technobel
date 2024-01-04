package be.technobel.employees.view.console;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public interface CustomLayout {
    LayoutData FILL_2 = GridLayout.createHorizontallyFilledLayoutData(2);
    LayoutData FILL_3 = GridLayout.createHorizontallyFilledLayoutData(3);
    LayoutData FILL_4 = GridLayout.createHorizontallyFilledLayoutData(4);
    LayoutData LEFT = GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER);
    LayoutData RIGHT = GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER);
    LayoutData CENTER = GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER);
    Panel PANEL_LINEAR_START = new Panel(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(
            LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning)
    );
    Panel PANEL_LINEAR_END = new Panel(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(
            LinearLayout.createLayoutData(LinearLayout.Alignment.End)
    );
    GridLayout GRID_LAYOUT_2 = new GridLayout(2).setHorizontalSpacing(3);
    GridLayout GRID_LAYOUT_3 = new GridLayout(3).setHorizontalSpacing(3);
    GridLayout GRID_LAYOUT_4 = new GridLayout(4).setHorizontalSpacing(3);
    TerminalSize TABLE_SIZE = new TerminalSize(32, 11);
    TerminalSize TABLE_SIZE_LARGE = new TerminalSize(64, 11);
    TerminalSize TEXT_BOX_SIZE = new TerminalSize(16, 1);
}
