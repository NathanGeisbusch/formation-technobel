package be.technobel.parsemaster.util;

import java.util.Arrays;
import static be.technobel.parsemaster.exception.Exceptions.NO_REMAINING_VERSION;
import static be.technobel.parsemaster.validation.util.Regex.PACKAGE_ID_SEP;
import be.technobel.parsemaster.exception.ConstraintException;

/** Represents the id of a parser/generator version. */
public record PackageId(String author, String name, long version) {
  /**
   * Creates a package id from an id string matching Regex.PACKAGE_ID.
   * @param id See Regex.PACKAGE_ID (must match the regex)
   */
  public static PackageId from(String id) {
    final var tokens = id.split(PACKAGE_ID_SEP);
    final var author = tokens[0];
    final var name = tokens[1];
    final var version = versionFromString(tokens[2]);
    return new PackageId(author, name, version);
  }

  /** Returns the string representation of the package id in the format: "author:package_name@0.0.1". */
  public String toString() {
    return author+":"+name+"@"+versionToString(version);
  }

  /**
   * Converts a package version from numeric format to text format.
   * @param version the version in numeric format (between 0 and Integer.MAX_VALUE)
   * @return the version in text format
   */
  public static String versionToString(long version) {
    return (version>>16L)+"."+((version>>8L)&0xff)+"."+(version&0xff);
  }

  /**
   * Converts a package version from text format to numeric format.
   * @param version the version in text format (must match Regex.VERSION)
   * @return the version in numeric format
   */
  public static long versionFromString(String version) {
    return Arrays.stream(version.split("\\."))
      .map(Long::parseLong)
      .reduce(0L, (acc,number) -> (acc<<8)|number);
  }

  /**
   * Returns the next available major version based on the current maximum version of a package.
   * @param maxVersion the current maximum version of a package (between 0 and Integer.MAX_VALUE)
   * @return the next available major version
   * @throws ConstraintException if there's no remaining available major version
   */
  public static long generateMajorVersion(long maxVersion) {
    long major = maxVersion & 0xffff0000L;
    if(major == 0xffff0000L) throw NO_REMAINING_VERSION.create();
    return (major + 0x10000L);
  }

  /**
   * Returns the next available minor version based on the current maximum version of a package.
   * @param maxVersion the current maximum version of a package (between 0 and Integer.MAX_VALUE)
   * @return the next available minor version
   * @throws ConstraintException if there's no remaining available minor version
   */
  public static long generateMinorVersion(long maxVersion) {
    long minor = maxVersion & 0xffffff00L;
    if((minor & 0x0000ff00L) == 0x0000ff00L) throw NO_REMAINING_VERSION.create();
    return minor + 0x100L;
  }

  /**
   * Returns the next available patch version based on the current maximum version of a package.
   * @param maxVersion the current maximum version of a package (between 0 and Integer.MAX_VALUE)
   * @return the next available patch version
   * @throws ConstraintException if there's no remaining available patch version
   */
  public static long generatePatchVersion(long maxVersion) {
    if((maxVersion & 0x000000ffL) == 0x000000ffL) throw NO_REMAINING_VERSION.create();
    return maxVersion + 1L;
  }
}
