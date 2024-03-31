package be.technobel.parsemaster.validation.util;

import java.util.regex.Pattern;

public interface Regex {
  String BYTE = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
  String SHORT = "([0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";
  String PACKAGE_VERSION = SHORT+"\\."+BYTE+"\\."+BYTE;
  String PACKAGE_NAME = "[\\p{L}\\-+_0-9]{1,64}";
  String PACKAGE_ID_SEP = "[:@]";

  String EMAIL = "^[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+$";
  String NAME = "^"+PACKAGE_NAME+"$";
  String VERSION = "^"+SHORT+"\\."+BYTE+"\\."+BYTE+"$";
  String PACKAGE_ID = "^"+PACKAGE_NAME+":"+PACKAGE_NAME+"@"+PACKAGE_VERSION+"$";

  Pattern PATTERN_VERSION = Pattern.compile(VERSION);
}
