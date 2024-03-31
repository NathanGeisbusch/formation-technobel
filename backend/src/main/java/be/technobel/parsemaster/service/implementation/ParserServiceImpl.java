package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.PackagesDeleteForm;
import be.technobel.parsemaster.form.ParserInfoForm;
import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.declaration.ParserConverter;
import be.technobel.parsemaster.service.declaration.ParserService;
import be.technobel.parsemaster.util.PackageId;
import be.technobel.parsemaster.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import static be.technobel.parsemaster.config.Config.FILE_UPLOAD_SIZE_LIMIT;
import static be.technobel.parsemaster.exception.Exceptions.*;

@Service
@Transactional
public class ParserServiceImpl implements ParserService {
  private final UserRepository userRepository;
  private final ParserRepository parserRepository;
  private final ParserVersionRepository versionRepository;
  private final PackageFileRepository fileRepository;
  private final ParserInteractionRepository interactionRepository;
  private final ParserConverter parserConverter;
  private final PasswordEncoder passwordEncoder;
  private final CompressionService compressionService;

  public ParserServiceImpl(
    UserRepository userRepository,
    ParserRepository parserRepository,
    ParserVersionRepository versionRepository,
    PackageFileRepository fileRepository,
    ParserInteractionRepository interactionRepository,
    ParserConverter parserConverter,
    PasswordEncoder passwordEncoder,
    CompressionService compressionService
  ) {
    this.userRepository = userRepository;
    this.parserRepository = parserRepository;
    this.versionRepository = versionRepository;
    this.fileRepository = fileRepository;
    this.interactionRepository = interactionRepository;
    this.parserConverter = parserConverter;
    this.passwordEncoder = passwordEncoder;
    this.compressionService = compressionService;
  }

  @Override
  @Transactional(readOnly = true)
  public PageDTO<PackagePublicDTO> findPublic(
    int page, int size, String search, SortPublicPackage sort, String userEmail
  ) {
    final var pageable = SortPublicPackage.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = versionRepository.findPublic(pageable, searchEscaped);
    return toPublicPage(result, userEmail);
  }

  @Override
  @Transactional(readOnly = true)
  public PageDTO<PackagePublicDTO> findBookmarked(
    int page, int size, String search, SortPublicPackage sort, String userEmail
  ) {
    final var pageable = SortPublicPackage.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = versionRepository.findBookmarked(pageable, searchEscaped, userEmail);
    return toPublicPage(result, userEmail);
  }

  @Override
  @Transactional(readOnly = true)
  public PageDTO<PackagePrivateDTO> findOwn(
    int page, int size, String search, Boolean allVersions,
    PackageVisibility visibility, SortPrivatePackage sort, String userEmail
  ) {
    final var pageable = SortPrivatePackage.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = versionRepository.findPrivate(
      pageable, searchEscaped, userEmail, allVersions, visibility
    );
    return new PageDTO<>(result.map(parserConverter::toPrivateDTO));
  }

  @Override
  @Transactional(readOnly = true)
  public PackagePublicDTO getPublic(PackageId id, String userEmail) {
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, null)
      .orElseThrow(PARSER_NOT_FOUND::create);
    final var interaction = interactionRepository.getByPackageId(id, userEmail).orElse(null);
    final var likes = interactionRepository.likesByPackageId(id);
    final var dislikes = interactionRepository.dislikesByPackageId(id);
    return parserConverter.toPublicDTO(version, interaction, likes, dislikes);
  }

  @Override
  @Transactional(readOnly = true)
  public PackagePrivateDTO getProtected(PackageId id, String password) {
    final var version = versionRepository
      .getIfCanAccess(id, null, password)
      .orElseThrow(PARSER_NOT_FOUND::create);
    if(version.getVisibility() != PackageVisibility.PROTECTED) throw FORBIDDEN.create();
    return parserConverter.toPrivateDTO(version);
  }

  @Override
  @Transactional(readOnly = true)
  public PackagePrivateDTO getPrivate(PackageId id, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, null)
      .orElseThrow(PARSER_NOT_FOUND::create);
    return parserConverter.toPrivateDTO(version);
  }

  @Override
  @Transactional(readOnly = true)
  public ParserEditDTO getEditable(PackageId id, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, null)
      .orElseThrow(PARSER_NOT_FOUND::create);
    return parserConverter.toEditDTO(version);
  }

  @Override
  public CreatedDTO create(PackageCreateForm form, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    final var existingParser = parserRepository.getByNameAndEmail(form.name(), userEmail);
    final ParserVersion entity;
    if(form.from() == null) {
      // CREATE NEW PARSER
      if(existingParser.isPresent()) throw PARSER_ALREADY_EXISTS.create();
      entity = parserConverter.create(user, form.name());
    } else {
      // FROM ORIGIN
      final var originId = PackageId.from(form.from());
      final var password = form.password() == null ? null : passwordEncoder.encode(form.password());
      if(!versionRepository.canAccess(originId, userEmail, password)) throw FORBIDDEN.create();
      final var origin = versionRepository
        .getByPackageId(originId)
        .orElseThrow(PARSER_NOT_FOUND::create);
      if(existingParser.isPresent()) {
        // MERGE ORIGIN VERSION INTO OWN PARSER
        final var existsByVersion = versionRepository
          .existsByNameAndVersion(form.name(), userEmail, originId.version());
        if(existsByVersion) throw PARSER_ALREADY_EXISTS.create();
        entity = parserConverter.merge(existingParser.get(), origin);
      } else {
        // COPY ORIGIN VERSION INTO NEW PARSER
        entity = parserConverter.copy(user, form.name(), origin);
      }
    }
    save(entity);
    final var id = new PackageId(user.getPseudonym(), form.name(), entity.getVersion());
    return new CreatedDTO(id.toString());
  }

  @Override
  public void update(PackageId id, ParserInfoForm form, String userEmail) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository.getByPackageId(id).orElseThrow(PARSER_NOT_FOUND::create);
    if(form.name() != null &&
      !form.name().equals(id.name()) &&
      parserRepository.existsByNameAndEmail(form.name(), userEmail)
    ) throw PARSER_ALREADY_EXISTS.create();
    parserConverter.update(version, form);
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
    final ParserInteraction interaction;
    if(existingInteraction.isEmpty()) {
      final Parser parser = parserRepository
        .getByNameAndAuthor(id.name(), id.author())
        .orElseThrow(PARSER_NOT_FOUND::create);
      interaction = new ParserInteraction(user, parser, value, false);
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
    final ParserInteraction interaction;
    if(existingInteraction.isEmpty()) {
      final Parser parser = parserRepository
        .getByNameAndAuthor(id.name(), id.author())
        .orElseThrow(PARSER_NOT_FOUND::create);
      interaction = new ParserInteraction(user, parser, null, value);
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
  @Transactional(readOnly = true)
  public byte[] getParserCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, ParserVersion::getParserFile);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] getBuilderCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, ParserVersion::getBuilderFile);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] getDocCode(PackageId id, String password, String userEmail) {
    return getCode(id, userEmail, password, ParserVersion::getDocFile);
  }

  @Override
  public void updateParserCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, ParserVersion::getParserFile);
  }

  @Override
  public void updateBuilderCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, ParserVersion::getBuilderFile);
  }

  @Override
  public void updateDocCode(PackageId id, HttpServletRequest request, String userEmail) {
    updateCode(id, request, userEmail, ParserVersion::getDocFile);
  }

  private void save(ParserVersion version) {
    parserRepository.save(version.getInfo());
    fileRepository.saveAll(List.of(
      version.getParserFile(),
      version.getBuilderFile(),
      version.getDocFile()
    ));
    versionRepository.save(version);
  }

  private void deleteVersions(PackageId id) {
    parserRepository.deleteVersions(id.name(), id.author());
    interactionRepository.deleteByPackageId(id);
    parserRepository.delete(id.name(), id.author());
  }

  private void deleteId(PackageId id) {
    final var version = versionRepository
      .getByPackageId(id)
      .orElseThrow(PARSER_NOT_FOUND::create);
    final var parser = version.getInfo();
    versionRepository.delete(version);
    if(versionRepository.remainingVersions(id.name(), id.author()) == 0) {
      interactionRepository.deleteByPackageId(id);
      parserRepository.delete(parser);
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
    if(maxVersion == null) throw PARSER_NOT_FOUND.create();
    long version = generateVersion.apply(maxVersion);
    final var origin = versionRepository
      .getByPackageId(new PackageId(id.author(), id.name(), maxVersion))
      .orElseThrow(PARSER_NOT_FOUND::create);
    final var result = parserConverter.createRevision(user, id.name(), origin, version);
    save(result);
    final var resultId = new PackageId(id.author(), id.name(), version);
    return new CreatedDTO(resultId.toString());
  }

  private byte[] getCode(
    PackageId id, String userEmail, String password,
    Function<ParserVersion, PackageFile> fileAccessor
  ) {
    final var version = versionRepository
      .getIfCanAccess(id, userEmail, password)
      .orElseThrow(PARSER_NOT_FOUND::create);
    return fileAccessor.apply(version).getContent();
  }

  private void updateCode(
    PackageId id, HttpServletRequest request, String userEmail,
    Function<ParserVersion, PackageFile> fileAccessor
  ) {
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    if(!user.getPseudonym().equals(id.author())) throw FORBIDDEN.create();
    final var version = versionRepository.getByPackageId(id).orElseThrow(PARSER_NOT_FOUND::create);
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

  private PageDTO<PackagePublicDTO> toPublicPage(Page<ParserVersion> versions, String userEmail) {
    return new PageDTO<>(versions.map(p -> {
      final var id = new PackageId(
        p.getInfo().getAuthor().getPseudonym(),
        p.getInfo().getName(),
        p.getVersion()
      );
      final var interaction = interactionRepository.getByPackageId(id, userEmail).orElse(null);
      final var likes = interactionRepository.likesByPackageId(id);
      final var dislikes = interactionRepository.dislikesByPackageId(id);
      return parserConverter.toPublicDTO(p, interaction, likes, dislikes);
    }));
  }
}
