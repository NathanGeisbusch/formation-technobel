package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.declaration.SearchService;
import be.technobel.parsemaster.util.PackageId;
import be.technobel.parsemaster.validation.util.Regex;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
  private final UserRepository userRepository;
  private final ParserRepository parserRepository;
  private final GeneratorRepository generatorRepository;
  private final SessionRepository sessionRepository;
  private final ParserVersionRepository parserVersionRepository;
  private final GeneratorVersionRepository generatorVersionRepository;

  public SearchServiceImpl(
    UserRepository userRepository,
    ParserRepository parserRepository,
    GeneratorRepository generatorRepository,
    SessionRepository sessionRepository,
    ParserVersionRepository parserVersionRepository,
    GeneratorVersionRepository generatorVersionRepository
  ) {
    this.userRepository = userRepository;
    this.parserRepository = parserRepository;
    this.generatorRepository = generatorRepository;
    this.sessionRepository = sessionRepository;
    this.parserVersionRepository = parserVersionRepository;
    this.generatorVersionRepository = generatorVersionRepository;
  }

  @Override
  public boolean existsPseudonym(String pseudonym) {
    return userRepository.existsByPseudonym(pseudonym);
  }

  @Override
  public boolean existsEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public boolean existsParserByName(Authentication auth, String name) {
    return parserRepository.existsByNameAndEmail(name, auth.getName());
  }

  @Override
  public boolean existsParserByVersion(Authentication auth, String name, String version) {
    if(!Regex.PATTERN_VERSION.matcher(version).matches()) return false;
    final var versionNumber = PackageId.versionFromString(version);
    return parserVersionRepository
      .existsByNameAndVersion(name, auth.getName(), versionNumber);
  }

  @Override
  public boolean existsGeneratorByName(Authentication auth, String name) {
    return generatorRepository.existsByNameAndEmail(name, auth.getName());
  }

  @Override
  public boolean existsGeneratorByVersion(Authentication auth, String name, String version) {
    if(!Regex.PATTERN_VERSION.matcher(version).matches()) return false;
    final var versionNumber = PackageId.versionFromString(version);
    return generatorVersionRepository
      .existsByNameAndVersion(name, auth.getName(), versionNumber);
  }

  @Override
  public boolean existsSession(Authentication auth, String name) {
    return sessionRepository.existsByName(name, auth.getName());
  }

  @Override
  public List<String> findParserVersions(
    Authentication auth, String author, String name, String version
  ) {
    final var pageable = PageRequest.of(0, 10, Sort.by("version").descending());
    final var userEmail = auth != null && auth.isAuthenticated() ? auth.getName() : null;
    final var range = getRange(version);
    if(range == null) return List.of();
    return parserVersionRepository
      .findVersions(pageable, userEmail, author, name, range[0], range[1])
      .getContent().stream().map(PackageId::versionToString).toList();
  }

  @Override
  public List<String> findGeneratorVersions(
    Authentication auth, String author, String name, String version
  ) {
    final var pageable = PageRequest.of(0, 10, Sort.by("version").descending());
    final var userEmail = auth != null && auth.isAuthenticated() ? auth.getName() : null;
    final var range = getRange(version);
    if(range == null) return List.of();
    return generatorVersionRepository
      .findVersions(pageable, userEmail, author, name, range[0], range[1])
      .getContent().stream().map(PackageId::versionToString).toList();
  }

  /**
   * Calculate the version search range from a version string.
   * @param version in the format "0.0.1" (see Regex.VERSION)
   * @return the version search range, or null if invalid version
   */
  private long[] getRange(String version) {
    try {
      final var tokens = version.replace("\\.$", "").split("\\.");
      final long min;
      final long max;
      if(version.isEmpty()) {
        min = 0;
        max = 0xffffffffL;
      }
      else if(tokens.length == 1) {
        final long major = Long.parseLong(tokens[0]);
        if(major > 0xffff) return null;
        min = major << 16;
        max = min | 0xffff;
      }
      else {
        final long major = Long.parseLong(tokens[0]);
        final long minor = Long.parseLong(tokens[1]);
        if(major > 0xffff || minor > 0xff) return null;
        min = (major << 16) | (minor << 8);
        max = min | 0xff;
      }
      return new long[] {min, max};
    }
    catch(NumberFormatException ex) {
      return null;
    }
  }
}
