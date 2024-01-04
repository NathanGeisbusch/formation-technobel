package be.technobel.heroes_vs_monsters.view;

public enum ANSI {
    //style
    BOLD("\033[1m"),
    ITALIC("\033[3m"),
    UNDERLINE("\033[4m"),
    BLINK("\033[5m"),
    LINE_BREAK("\n"),

    //color fg
    BLACK("\033[30m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    MAGENTA("\033[35m"),
    CYAN("\033[36m"),
    WHITE("\033[37m"),

    //color bg
    BG_BLACK("\033[40m"),
    BG_RED("\033[41m"),
    BG_GREEN("\033[42m"),
    BG_YELLOW("\033[43m"),
    BG_BLUE("\033[44m"),
    BG_MAGENTA("\033[45m"),
    BG_CYAN("\033[46m"),
    BG_WHITE("\033[47m"),

    //style off
    RESET("\033[0m"),
    BOLD_OFF("\033[21m"),
    ITALIC_OFF("\033[23m"),
    UNDERLINE_OFF("\033[24m"),
    BLINK_OFF("\033[25m"),
    DEFAULT("\033[39m"),
    BG_DEFAULT("\033[39m");

    private final String value;

    ANSI(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(ANSI c) {
        return this.value.equals(c.value);
    }

    public boolean equals(String s) {
        return this.value.equals(s);
    }

    public static ANSI getANSI(String s) {
        for(ANSI c : ANSI.values()) if(c.equals(s)) return c;
        return null;
    }
}
