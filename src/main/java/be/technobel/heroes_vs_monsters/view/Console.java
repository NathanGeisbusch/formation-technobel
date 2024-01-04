package be.technobel.heroes_vs_monsters.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

//! sur windows, utiliser javaw.exe au lieu de java.exe
//"C:\Program Files\Java\jdk-20\bin\javaw.exe" -Dsun.java2d.dpiaware=true -Dsun.java2d.uiScale=true -Dsun.java2d.autoScaleThreshold=1.5 -Dsun.java2d.uiScale=1.0
//https://github.com/mabe02/lanterna/blob/master/docs/contents.md

public class Console {
    private final Terminal terminal;
    Console(String title) {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setTerminalEmulatorTitle(title);
            terminalFactory.setTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode);
            //Si Windows: assigner une font monospace compatible unicode.
            if(System.getProperty("os.name").startsWith("Windows") && doesFontExists("Consolas")) {
                Font font = new Font ("Consolas", Font.PLAIN, 20);
                SwingTerminalFontConfiguration swingFont = new SwingTerminalFontConfiguration(
                        true, AWTTerminalFontConfiguration.BoldMode.NOTHING, font);
                terminalFactory.setTerminalEmulatorFontConfiguration(swingFont);
            }
            else if(doesFontExists("Monospaced")){
                Font font = new Font ("Monospaced", Font.PLAIN, 20);
                SwingTerminalFontConfiguration swingFont = new SwingTerminalFontConfiguration(
                        true, AWTTerminalFontConfiguration.BoldMode.NOTHING, font);
                terminalFactory.setTerminalEmulatorFontConfiguration(swingFont);
            }
            this.terminal = terminalFactory.createTerminal();
            //Si terminal graphique : définir que l'app doit se terminer quand la fenêtre est fermée.
            if(this.terminal instanceof SwingTerminalFrame) {
                final SwingTerminalFrame frame = (SwingTerminalFrame)terminal;
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void close() {
        if(this.terminal != null) {
            try {
                this.terminal.exitPrivateMode();
                this.terminal.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void stop() {
        if(this.terminal != null) {
            try {
                this.terminal.exitPrivateMode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void init() {
        try {
            terminal.enterPrivateMode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public char getChar() {
        try {
            KeyStroke ks = terminal.readInput();
            if(ks != null) {
                KeyType kt = ks.getKeyType();
                if(kt == KeyType.Character) return ks.getCharacter();
                else if(kt == KeyType.ArrowUp) return '↑';
                else if(kt == KeyType.ArrowDown) return '↓';
                else if(kt == KeyType.ArrowLeft) return '←';
                else if(kt == KeyType.ArrowRight) return '→';
            }
            return 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public KeyType getKey() {
        try {
            KeyStroke ks = terminal.readInput();
            if(ks != null) {
                return ks.getKeyType();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Console clear() {
        try {
            terminal.clearScreen();
            terminal.setCursorPosition(0, 0);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Console append(Object... args) {
        try {
            for(Object arg: args) {
                String s = arg.toString();
                ANSI c = ANSI.getANSI(s);
                if(c == null) {
                    if(s.indexOf('\n') == -1) terminal.putString(s);
                    else {
                        String[] lines = s.split("\n");
                        for(String line : lines) {
                            terminal.putString(line);
                            terminal.putCharacter('\n');
                        }
                    }
                    continue;
                }
                switch(c) {
                    case BOLD -> terminal.enableSGR(SGR.BOLD);
                    case ITALIC -> terminal.enableSGR(SGR.ITALIC);
                    case UNDERLINE -> terminal.enableSGR(SGR.UNDERLINE);
                    case BLINK -> terminal.enableSGR(SGR.BLINK);
                    case BOLD_OFF -> terminal.disableSGR(SGR.BOLD);
                    case ITALIC_OFF -> terminal.disableSGR(SGR.ITALIC);
                    case UNDERLINE_OFF -> terminal.disableSGR(SGR.UNDERLINE);
                    case BLINK_OFF -> terminal.disableSGR(SGR.BLINK);
                    case RESET -> terminal.resetColorAndSGR();
                    case DEFAULT -> terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
                    case BG_DEFAULT -> terminal.setBackgroundColor(TextColor.ANSI.DEFAULT);
                    case BLACK -> terminal.setForegroundColor(TextColor.ANSI.BLACK);
                    case RED -> terminal.setForegroundColor(TextColor.ANSI.RED);
                    case GREEN -> terminal.setForegroundColor(TextColor.ANSI.GREEN);
                    case YELLOW -> terminal.setForegroundColor(TextColor.ANSI.YELLOW);
                    case BLUE -> terminal.setForegroundColor(TextColor.ANSI.BLUE);
                    case MAGENTA -> terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                    case CYAN -> terminal.setForegroundColor(TextColor.ANSI.CYAN);
                    case WHITE -> terminal.setForegroundColor(TextColor.ANSI.WHITE);
                    case BG_BLACK -> terminal.setBackgroundColor(TextColor.ANSI.BLACK);
                    case BG_RED -> terminal.setBackgroundColor(TextColor.ANSI.RED);
                    case BG_GREEN -> terminal.setBackgroundColor(TextColor.ANSI.GREEN);
                    case BG_YELLOW -> terminal.setBackgroundColor(TextColor.ANSI.YELLOW);
                    case BG_BLUE -> terminal.setBackgroundColor(TextColor.ANSI.BLUE);
                    case BG_MAGENTA -> terminal.setBackgroundColor(TextColor.ANSI.MAGENTA);
                    case BG_CYAN -> terminal.setBackgroundColor(TextColor.ANSI.CYAN);
                    case BG_WHITE -> terminal.setBackgroundColor(TextColor.ANSI.WHITE);
                    case LINE_BREAK -> terminal.putCharacter('\n');
                }
            }
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Console show() {
        try {
            terminal.flush();
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Console print(Object... args) {
        return this.append(args).show();
    }
    private static boolean doesFontExists(String name) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(String font : fonts) if(name.equals(font)) return true;
        return false;
    }
}
