package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.PackagePrivateDTO;
import be.technobel.parsemaster.dto.PackagePublicDTO;
import be.technobel.parsemaster.dto.ParserEditDTO;
import be.technobel.parsemaster.entity.Parser;
import be.technobel.parsemaster.entity.ParserVersion;
import be.technobel.parsemaster.entity.ParserInteraction;
import be.technobel.parsemaster.entity.User;
import be.technobel.parsemaster.form.ParserInfoForm;

public interface ParserConverter {
  PackagePublicDTO toPublicDTO(
    ParserVersion version,
    ParserInteraction interaction,
    long likes, long dislikes
  );

  PackagePrivateDTO toPrivateDTO(ParserVersion version);

  ParserEditDTO toEditDTO(ParserVersion version);

  /**
   * Creates a new parser version.
   * @param author author (current user)
   * @param name name of the new parser
   * @return the new parser version
   */
  ParserVersion create(User author, String name);

  /**
   * Creates a new parser version from a parser.
   * @param author author (current user)
   * @param name name of the new parser
   * @param origin original parser version
   * @return the new parser version
   */
  ParserVersion copy(User author, String name, ParserVersion origin);

  /**
   * Creates a new parser version (major, minor, patch).
   * @param author author (current user)
   * @param name name of the new parser
   * @param origin original parser version
   * @param version version to assign to new generation (if null, defaults to 0)
   * @return the new parser version
   */
  ParserVersion createRevision(User author, String name, ParserVersion origin, Long version);

  /**
   * Creates a new parser version from a parser and merge it into an existing parser.
   * @param existing existing parser
   * @param origin original parser version
   * @return the new parser version
   */
  ParserVersion merge(Parser existing, ParserVersion origin);

  /**
   * Updates a parser from a form.
   * @param version existing parser version
   * @param form form containing new parser information
   */
  void update(ParserVersion version, ParserInfoForm form);
}
