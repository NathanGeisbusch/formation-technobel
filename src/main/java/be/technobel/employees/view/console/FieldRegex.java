package be.technobel.employees.view.console;

import java.util.regex.Pattern;

/** Validation en temps réel de champ de texte éditable */
public enum FieldRegex {
    DATE("""
        ([1-9]\
        |[1-9][0-9]{0,8}\
        |[1-9][0-9]{0,8}-\
        |[1-9][0-9]{0,8}-([0-1]|0[1-9]|1[0-2])\
        |[1-9][0-9]{0,8}-(0[1-9]|1[0-2])-\
        |[1-9][0-9]{0,8}-(0[1-9]|1[0-2])-([0-3]|0[1-9]|[1-2][0-9]|3[0-1])\
        )"""),
    SALARY("""
        (([0-9]|[1-9][0-9]+)|\
        ([0-9]|[1-9][0-9]+)\\.|\
        ([0-9]|[1-9][0-9]+)\\.[0-9]{0,2})""");
    private final Pattern regex;
    FieldRegex(String value) {
        this.regex = Pattern.compile(value.trim());
    }
    public Pattern value() {
        return this.regex;
    }
}
