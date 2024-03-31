package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.PackagePrivateDTO;
import be.technobel.parsemaster.dto.PackagePublicDTO;
import be.technobel.parsemaster.dto.GeneratorEditDTO;
import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.form.GeneratorInfoForm;

public interface GeneratorConverter {
  PackagePublicDTO toPublicDTO(
    GeneratorVersion version,
    GeneratorInteraction interaction,
    long likes, long dislikes
  );

  PackagePrivateDTO toPrivateDTO(GeneratorVersion version);

  GeneratorEditDTO toEditDTO(GeneratorVersion version);

  /**
   * Creates a new generator version.
   * @param author author (current user)
   * @param name name of the new generator
   * @return the new generator version
   */
  GeneratorVersion create(User author, String name);

  /**
   * Creates a new generator version from a generator version.
   * @param author author (current user)
   * @param name name of the new generator
   * @param origin original generator version
   * @return the new generator version
   */
  GeneratorVersion copy(User author, String name, GeneratorVersion origin);

  /**
   * Creates a new generator version (major, minor, patch).
   * @param author author (current user)
   * @param name name of the new generator
   * @param origin original generator version
   * @param version version to assign to new generation
   * @return the new generator version
   */
  GeneratorVersion createRevision(User author, String name, GeneratorVersion origin, Long version);

  /**
   * Creates a new generator version from a parser.
   * @param author author (current user)
   * @param name name of the new generator
   * @param origin original parser version
   * @return the new generator version
   */
  GeneratorVersion copy(User author, String name, ParserVersion origin);

  /**
   * Creates a new generator version from a generator and merge it into an existing generator.
   * @param existing existing generator
   * @param origin original generator version
   * @return the new generator version
   */
  GeneratorVersion merge(Generator existing, GeneratorVersion origin);

  /**
   * Updates a generator from a form.
   * @param version existing generator version
   * @param form form containing new generator information
   */
  void update(GeneratorVersion version, GeneratorInfoForm form);
}
