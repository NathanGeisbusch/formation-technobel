package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.dto.PageDTO;
import be.technobel.parsemaster.dto.SessionDTO;
import be.technobel.parsemaster.entity.Session;
import be.technobel.parsemaster.enumeration.SortSession;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.form.SessionCreateForm;
import be.technobel.parsemaster.form.SessionEditForm;
import be.technobel.parsemaster.form.SessionsDeleteForm;
import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.declaration.SessionConverter;
import be.technobel.parsemaster.service.declaration.SessionService;
import be.technobel.parsemaster.util.PackageId;
import be.technobel.parsemaster.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import static be.technobel.parsemaster.config.Config.FILE_UPLOAD_SIZE_LIMIT;
import static be.technobel.parsemaster.exception.Exceptions.*;

@Service
public class SessionServiceImpl implements SessionService {
  private final SessionRepository sessionRepository;
  private final SessionConverter sessionConverter;
  private final GeneratorVersionRepository generatorVersionRepository;
  private final PackageFileRepository fileRepository;
  private final UserRepository userRepository;
  private final CompressionService compressionService;

  public SessionServiceImpl(
    SessionRepository sessionRepository,
    SessionConverter sessionConverter,
    GeneratorVersionRepository generatorVersionRepository,
    PackageFileRepository fileRepository,
    UserRepository userRepository,
    CompressionService compressionService
  ) {
    this.sessionRepository = sessionRepository;
    this.sessionConverter = sessionConverter;
    this.generatorVersionRepository = generatorVersionRepository;
    this.fileRepository = fileRepository;
    this.userRepository = userRepository;
    this.compressionService = compressionService;
  }

  @Override
  public PageDTO<SessionDTO> find(
    Integer page, Integer size, String search, SortSession sort, String userEmail
  ) {
    final var pageable = SortSession.getPageable(page, size, sort);
    final var searchEscaped = search == null ? null : Utils.escapeLikePattern(search);
    final var result = sessionRepository.find(pageable, searchEscaped, userEmail);
    return new PageDTO<>(result.map(sessionConverter::toDTO));
  }

  @Override
  public SessionDTO get(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    return sessionConverter.toDTO(session);
  }

  @Override
  public CreatedDTO create(SessionCreateForm form, String userEmail) {
    if(sessionRepository.existsByName(form.name(), userEmail)) {
      throw SESSION_ALREADY_EXISTS.create();
    }
    final var generator = generatorVersionRepository
      .getByPackageId(PackageId.from(form.from()))
      .orElseThrow(GENERATOR_NOT_FOUND::create);
    final var user = userRepository.findByEmail(userEmail).orElseThrow(USER_NOT_FOUND::create);
    final var session = new Session(generator, form.name(), user, compressionService.createFile());
    fileRepository.save(session.getInputFile());
    sessionRepository.save(session);
    return new CreatedDTO(form.name());
  }

  @Override
  public void update(String id, SessionEditForm form, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    if(form.name() != null &&
      !form.name().equals(id) &&
      sessionRepository.existsByName(form.name(), userEmail)
    ) throw SESSION_ALREADY_EXISTS.create();
    session.setName(form.name());
    session.setUpdatedAt(LocalDateTime.now());
    sessionRepository.save(session);
  }

  @Override
  public void delete(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    final var generatorVersion = session.getGeneratorVersion();
    final var generator = generatorVersion.getInfo();
    sessionRepository.delete(session);
    sessionRepository.deleteGeneratorVersion(generatorVersion.getId());
    sessionRepository.deleteGenerator(generator.getId());
  }

  @Override
  public void delete(SessionsDeleteForm form, String userEmail) {
    for(final var id : form.id()) delete(id, userEmail);
  }

  @Override
  public byte[] getInputText(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    return session.getInputFile().getContent();
  }

  @Override
  public byte[] getParserCode(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    return session.getGeneratorVersion().getParserFile().getContent();
  }

  @Override
  public byte[] getBuilderCode(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    return session.getGeneratorVersion().getBuilderFile().getContent();
  }

  @Override
  public byte[] getGeneratorCode(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    return session.getGeneratorVersion().getGeneratorFile().getContent();
  }

  @Override
  public byte[] getDocCode(String id, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    return session.getGeneratorVersion().getDocFile().getContent();
  }

  @Override
  public void updateInputText(String id, HttpServletRequest request, String userEmail) {
    final var session = sessionRepository.getByName(id, userEmail)
      .orElseThrow(SESSION_NOT_FOUND::create);
    final var file = session.getInputFile();
    final byte[] payload;
    try(final var body = request.getInputStream()) {
      payload = compressionService.readAndValidate(body, FILE_UPLOAD_SIZE_LIMIT);
    } catch(IOException e) {
      throw Exceptions.INVALID_PAYLOAD.create();
    }
    file.setContent(payload);
    session.setUpdatedAt(LocalDateTime.now());
    fileRepository.save(file);
    sessionRepository.save(session);
  }
}
