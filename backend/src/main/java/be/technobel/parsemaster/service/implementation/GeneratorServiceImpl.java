package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.form.GeneratorInfoForm;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.PackagesDeleteForm;
import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.declaration.GeneratorConverter;
import be.technobel.parsemaster.service.declaration.GeneratorService;
import be.technobel.parsemaster.util.PackageId;
import be.technobel.parsemaster.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import static be.technobel.parsemaster.config.Config.FILE_UPLOAD_SIZE_LIMIT;
import static be.technobel.parsemaster.exception.Exceptions.*;
import static be.technobel.parsemaster.exception.Exceptions.GENERATOR_NOT_FOUND;

@Service
public class GeneratorServiceImpl implements GeneratorService {
  private final GeneratorRepository generatorRepository;
  private final GeneratorVersionRepository versionRepository;
  private final GeneratorInteractionRepository interactionRepository;
  private final GeneratorConverter generatorConverter;
  private final ParserVersionRepository parserVersionRepository;
  private final PackageFileRepository fileRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CompressionService compressionService;

  public GeneratorServiceImpl(
    GeneratorRepository generatorRepository,
    GeneratorVersionRepository versionRepository,
    GeneratorInteractionRepository interactionRepository,
    GeneratorConverter generatorConverter,
    ParserVersionRepository parserVersionRepository,
    PackageFileRepository fileRepository,
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    CompressionService compressionService
  ) {
    this.generatorRepository = generatorRepository;
    this.versionRepository = versionRepository;
    this.interactionRepository = interactionRepository;
    this.generatorConverter = generatorConverter;
    this.parserVersionRepository = parserVersionRepository;
    this.fileRepository = fileRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.compressionService = compressionService;
  }

  @Override
  public PageDTO<PackagePublicDTO> findPublic(
    int page, int size, String search, SortPublicPackage sort, String userEmail
  ) {
    final var pageable = SortPublicPackage.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = versionRepository.findPublic(pageable, searchEscaped);
    return toPublicPage(result, userEmail);
  }

  @Override
  public PageDTO<PackagePublicDTO> findBookmarked(
    int page, int size, String search, SortPublicPackage sort, String userEmail
  ) {
    final var pageable = SortPublicPackage.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = versionRepository.findBookmarked(pageable, searchEscaped, userEmail);
    return toPublicPage(result, userEmail);
  }

  @Override
  public PageDTO<PackagePrivateDTO> findOwn(
    int page, int size, String search, Boolean allVersions,
    PackageVisibility visibility, SortPrivatePackage sort, String userEmail
  ) {
    final var pageable = SortPrivatePackage.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = versionRepository.findPrivate(
      pageable, searchEscaped, userEmail, allVersions, visibility
    );
    return new PageDTO<>(result.map(generatorConverter::toPrivateDTO));
  }

  @Override
  public PackagePublicDTO getPublic(PackageId id, String userEmail) {
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, null)
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    final var interaction = interactionRepository.getByPackageId(id, userEmail).orElse(null);
    final var likes = interactionRepository.likesByPackageId(id);
    final var dislikes = interactionRepository.dislikesByPackageId(id);
    return generatorConverter.toPublicDTO(version, interaction, likes, dislikes);
  }

  @Override
  public PackagePrivateDTO getProtected(PackageId id, String password) {
    final var version = versionRepository
      .getIfCanAccess(id, null, password)
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    if(version.getVisibility() != PackageVisibility.PROTECTED) throw FORBIDDEN.create();
    return generatorConverter.toPrivateDTO(version);
  }

  @Override
  public PackagePrivateDTO getPrivate(PackageId id, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, null)
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    return generatorConverter.toPrivateDTO(version);
  }

  @Override
  public GeneratorEditDTO getEditable(PackageId id, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, null)
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    return generatorConverter.toEditDTO(version);
  }

  @Override
  public CreatedDTO create(PackageCreateForm form, Boolean fromParser, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    final var existingGenerator = generatorRepository.getByNameAndEmail(form.name(), userEmail);
    final GeneratorVersion entity;
    if(form.from() == null) {
      // CREATE NEW GENERATOR
      if(existingGenerator.isPresent()) throw PARSER_ALREADY_EXISTS.create();
      entity = generatorConverter.create(user, form.name());
    } else {
      // FROM ORIGIN
      final var originId = PackageId.from(form.from());
      final var password = form.password() == null ? null : passwordEncoder.encode(form.password());
      if(existingGenerator.isPresent()) {
        // MERGE ORIGIN VERSION INTO OWN GENERATOR
        if(!versionRepository.canAccess(originId, userEmail, password)) throw FORBIDDEN.create();
        final var origin = versionRepository
          .getByPackageId(originId)
          .orElseThrow(GENERATOR_NOT_FOUND::create);
        final var existsByVersion = versionRepository
          .existsByNameAndVersion(form.name(), userEmail, originId.version());
        if(existsByVersion) throw PARSER_ALREADY_EXISTS.create();
        entity = generatorConverter.merge(existingGenerator.get(), origin);
      } else {
        // COPY ORIGIN VERSION INTO NEW GENERATOR
        if(fromParser != null && fromParser) {
          // FROM PARSER
          if(!parserVersionRepository.canAccess(originId, userEmail, password)) throw FORBIDDEN.create();
          final var origin = parserVersionRepository
            .getByPackageId(originId)
            .orElseThrow(PARSER_NOT_FOUND::create);
          entity = generatorConverter.copy(user, form.name(), origin);
        } else {
          // FROM GENERATOR
          if(!versionRepository.canAccess(originId, userEmail, password)) throw FORBIDDEN.create();
          final var origin = versionRepository
            .getByPackageId(originId)
            .orElseThrow(GENERATOR_NOT_FOUND::create);
          entity = generatorConverter.copy(user, form.name(), origin);
        }
      }
    }
    save(entity);
    final var id = new PackageId(user.getPseudonym(), form.name(), entity.getVersion());
    return new CreatedDTO(id.toString());
  }

  @Override
  public void update(PackageId id, GeneratorInfoForm form, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository.getByPackageId(id).orElseThrow(GENERATOR_NOT_FOUND::create);
    if(form.name() != null &&
      !form.name().equals(id.name()) &&
      generatorRepository.existsByNameAndEmail(form.name(), userEmail)
    ) throw PARSER_ALREADY_EXISTS.create();
    generatorConverter.update(version, form);
    versionRepository.save(version);
  }

  @Override
  public void delete(PackageId id, Boolean allVersions, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    if(allVersions != null && allVersions) deleteVersions(id);
    else deleteId(id);
  }

  @Override
  public void delete(PackagesDeleteForm form, Boolean allVersions, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    for(final var formId : form.id()) {
      final var id = PackageId.from(formId);
      if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
      if(allVersions != null && allVersions) deleteVersions(id);
      else deleteId(id);
    }
  }

  @Override
  public void like(PackageId id, Boolean value, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var existingInteraction = interactionRepository.getByPackageId(id, userEmail);
    final GeneratorInteraction interaction;
    if(existingInteraction.isEmpty()) {
      final Generator generator = generatorRepository.
        getByNameAndAuthor(id.name(), id.author())
        .orElseThrow(GENERATOR_NOT_FOUND::create);
      interaction = new GeneratorInteraction(user, generator, value, false);
    } else {
      interaction = existingInteraction.get();
      interaction.setLiked(value);
    }
    interactionRepository.save(interaction);
  }

  @Override
  public void bookmark(PackageId id, Boolean value, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var existingInteraction = interactionRepository.getByPackageId(id, userEmail);
    final GeneratorInteraction interaction;
    if(existingInteraction.isEmpty()) {
      final Generator generator = generatorRepository.
        getByNameAndAuthor(id.name(), id.author())
        .orElseThrow(GENERATOR_NOT_FOUND::create);
      interaction = new GeneratorInteraction(user, generator, null, value);
    } else {
      interaction = existingInteraction.get();
      interaction.setBookmarked(value);
    }
    interactionRepository.save(interaction);
  }

  @Override
  public CreatedDTO createMajorVersion(PackageId id, String userEmail) {
    return createVersion(id, userEmail, PackageId::generateMajorVersion);
  }

  @Override
  public CreatedDTO createMinorVersion(PackageId id, String userEmail) {
    return createVersion(id, userEmail, PackageId::generateMinorVersion);
  }

  @Override
  public CreatedDTO createPatchVersion(PackageId id, String userEmail) {
    return createVersion(id, userEmail, PackageId::generatePatchVersion);
  }

  @Override
  public byte[] getParserCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, GeneratorVersion::getParserFile);
  }

  @Override
  public byte[] getBuilderCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, GeneratorVersion::getBuilderFile);
  }

  @Override
  public byte[] getGeneratorCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, GeneratorVersion::getGeneratorFile);
  }

  @Override
  public byte[] getDocCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, GeneratorVersion::getDocFile);
  }

  @Override
  public void updateParserCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, GeneratorVersion::getParserFile);
  }

  @Override
  public void updateBuilderCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, GeneratorVersion::getBuilderFile);
  }

  @Override
  public void updateGeneratorCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, GeneratorVersion::getGeneratorFile);
  }

  @Override
  public void updateDocCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, GeneratorVersion::getDocFile);
  }

  private void save(GeneratorVersion version) {
    generatorRepository.save(version.getInfo());
    fileRepository.saveAll(List.of(
      version.getParserFile(),
      version.getBuilderFile(),
      version.getGeneratorFile(),
      version.getDocFile()
    ));
    versionRepository.save(version);
  }

  private void deleteVersions(PackageId id) {
    generatorRepository.deleteVersions(id.name(), id.author());
    generatorRepository.disableVersions(id.name(), id.author());
    interactionRepository.deleteByPackageId(id);
    generatorRepository.delete(id.name(), id.author());
    generatorRepository.disable(id.name(), id.author());
  }

  private void deleteId(PackageId id) {
    versionRepository.getByPackageId(id).orElseThrow(GENERATOR_NOT_FOUND::create);
    versionRepository.deleteVersion(id);
    versionRepository.disableVersion(id);
    if(versionRepository.remainingVersions(id.name(), id.author()) == 0) {
      interactionRepository.deleteByPackageId(id);
      generatorRepository.delete(id.name(), id.author());
      generatorRepository.disable(id.name(), id.author());
    }
  }

  private CreatedDTO createVersion(
    PackageId id, String userEmail, Function<Long, Long> generateVersion
  ) {
    final var user = userRepository
      .findByEmail(userEmail)
      .orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final Long maxVersion = versionRepository.getMaxVersion(id);
    if(maxVersion == null) throw GENERATOR_NOT_FOUND.create();
    long version = generateVersion.apply(maxVersion);
    final var origin = versionRepository
      .getByPackageId(new PackageId(id.author(), id.name(), maxVersion))
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    final var result = generatorConverter.createRevision(user, id.name(), origin, version);
    save(result);
    final var resultId = new PackageId(id.author(), id.name(), version);
    return new CreatedDTO(resultId.toString());
  }

  private byte[] getCode(
    PackageId id, String userEmail, String password,
    Function<GeneratorVersion, PackageFile> fileAccessor
  ) {
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, password)
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    return fileAccessor.apply(version).getContent();
  }

  private void updateCode(
    PackageId id, HttpServletRequest request, String userEmail,
    Function<GeneratorVersion, PackageFile> fileAccessor
  ) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository.getByPackageId(id).orElseThrow(GENERATOR_NOT_FOUND::create);
    final var file = fileAccessor.apply(version);
    final byte[] payload;
    try(final var body = request.getInputStream()) {
      payload = compressionService.readAndValidate(body, FILE_UPLOAD_SIZE_LIMIT);
    } catch(IOException e) {
      throw Exceptions.INVALID_PAYLOAD.create();
    }
    file.setContent(payload);
    version.setUpdatedAt(LocalDateTime.now());
    fileRepository.save(file);
    versionRepository.save(version);
  }

  private PageDTO<PackagePublicDTO> toPublicPage(Page<GeneratorVersion> versions, String userEmail) {
    return new PageDTO<>(versions.map(p -> {
      final var id = new PackageId(
        p.getInfo().getAuthor().getPseudonym(),
        p.getInfo().getName(),
        p.getVersion()
      );
      final var interaction = interactionRepository.getByPackageId(id, userEmail).orElse(null);
      final var likes = interactionRepository.likesByPackageId(id);
      final var dislikes = interactionRepository.dislikesByPackageId(id);
      return generatorConverter.toPublicDTO(p, interaction, likes, dislikes);
    }));
  }
}
