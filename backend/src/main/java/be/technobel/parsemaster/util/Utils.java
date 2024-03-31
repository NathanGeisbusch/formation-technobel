package be.technobel.parsemaster.util;

import java.util.regex.Pattern;

public abstract class Utils {
  /** Reserved characters in SQL "LIKE" pattern that must be escaped. */
  private static final Pattern ESCAPE_LIKE_RESERVED = Pattern.compile("[!%_\\[]");

  /** Escape character to use in SQL "LIKE" operation. */
  public static final char ESCAPE_CHAR = '!';

  /**
   * Escape a text for use in SQL "LIKE" pattern.
   * @param text text to escape
   * @return the uppercase escaped result
   */
  public static String escapeLikePattern(String text) {
    final var matcher = ESCAPE_LIKE_RESERVED.matcher(text);
    final var builder = new StringBuilder();
    while(matcher.find()) matcher.appendReplacement(builder, "!" + matcher.group());
    return matcher.appendTail(builder).toString().toUpperCase();
  }
}
