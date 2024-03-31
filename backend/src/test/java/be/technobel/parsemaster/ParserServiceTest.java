package be.technobel.parsemaster;

import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.dto.PageDTO;
import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.exception.ConstraintException;
import be.technobel.parsemaster.exception.ForbiddenException;
import be.technobel.parsemaster.exception.NotFoundException;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.ParserInfoForm;
import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.implementation.ParserConverterImpl;
import be.technobel.parsemaster.service.implementation.ParserServiceImpl;
import be.technobel.parsemaster.util.PackageId;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import static be.technobel.parsemaster.exception.Exceptions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("testing")
@ExtendWith(MockitoExtension.class)
public class ParserServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private ParserRepository parserRepository;
  @Mock private ParserVersionRepository versionRepository;
  @Mock private PackageFileRepository fileRepository;
  @Mock private ParserInteractionRepository interactionRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private CompressionService compressionService;
  @InjectMocks private ParserConverterImpl parserConverter;
  @InjectMocks private ParserServiceImpl parserService;

  private User user;

  @BeforeEach
  public void setUp() {
    this.parserService = new ParserServiceImpl(
      userRepository, parserRepository, versionRepository, fileRepository,
      interactionRepository, parserConverter, passwordEncoder, compressionService
    );
    user = new User("user", "user@test.be", "123");
  }

  @Test
  public void findPublic() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(info.getAuthor()).thenReturn(author);
    when(version.getInfo()).thenReturn(info);
    var interaction = mock(ParserInteraction.class);
    var likes = 1L;
    var sort = mock(SortPublicPackage.class);
    var expected = new PageDTO<>(1, 1, 0, 1, List.of(
      parserConverter.toPublicDTO(version, interaction, likes, likes)
    ));
    when(versionRepository.findPublic(any(Pageable.class), anyString()))
      .thenReturn(new PageImpl<>(List.of(version)));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.of(interaction));
    when(interactionRepository.likesByPackageId(any(PackageId.class))).thenReturn(likes);
    when(interactionRepository.dislikesByPackageId(any(PackageId.class))).thenReturn(likes);
    var actual = parserService.findPublic(
      expected.page(), expected.size(), "", sort, user.getEmail()
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findBookmarked() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(info.getAuthor()).thenReturn(author);
    when(version.getInfo()).thenReturn(info);
    var interaction = mock(ParserInteraction.class);
    var likes = 1L;
    var sort = mock(SortPublicPackage.class);
    var expected = new PageDTO<>(1, 1, 0, 1, List.of(
      parserConverter.toPublicDTO(version, interaction, likes, likes)
    ));
    when(versionRepository.findBookmarked(any(Pageable.class), anyString(), anyString()))
      .thenReturn(new PageImpl<>(List.of(version)));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.of(interaction));
    when(interactionRepository.likesByPackageId(any(PackageId.class))).thenReturn(likes);
    when(interactionRepository.dislikesByPackageId(any(PackageId.class))).thenReturn(likes);
    var actual = parserService.findBookmarked(
      expected.page(), expected.size(), "", sort, user.getEmail()
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findOwn() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(info.getAuthor()).thenReturn(author);
    when(version.getInfo()).thenReturn(info);
    var visibility = mock(PackageVisibility.class);
    var sort = mock(SortPrivatePackage.class);
    var expected = new PageDTO<>(1, 1, 0, 1, List.of(
      parserConverter.toPrivateDTO(version)
    ));
    when(versionRepository.findPrivate(
      any(Pageable.class), anyString(), anyString(), anyBoolean(), any(PackageVisibility.class)
    )).thenReturn(new PageImpl<>(List.of(version)));
    var actual = parserService.findOwn(
      expected.page(), expected.size(), "",
      true, visibility, sort, user.getEmail()
    );
    assertEquals(expected, actual);
  }

  @Test
  public void getPublic_ok() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(info.getAuthor()).thenReturn(author);
    when(version.getInfo()).thenReturn(info);
    var interaction = mock(ParserInteraction.class);
    var likes = 1L;
    var id = mock(PackageId.class);
    var expected = parserConverter.toPublicDTO(version, interaction, likes, likes);
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), nullable(String.class)))
      .thenReturn(Optional.of(version));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.of(interaction));
    when(interactionRepository.likesByPackageId(any(PackageId.class))).thenReturn(likes);
    when(interactionRepository.dislikesByPackageId(any(PackageId.class))).thenReturn(likes);
    var actual = parserService.getPublic(id, "");
    assertEquals(expected, actual);
  }

  @Test
  public void getPublic_not_found() {
    var id = mock(PackageId.class);
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), nullable(String.class)))
      .thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.getPublic(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void getProtected_ok() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(info.getAuthor()).thenReturn(author);
    when(version.getInfo()).thenReturn(info);
    when(version.getVisibility()).thenReturn(PackageVisibility.PROTECTED);
    var id = mock(PackageId.class);
    var expected = parserConverter.toPrivateDTO(version);
    when(versionRepository.getIfCanAccess(any(PackageId.class), nullable(String.class), anyString()))
      .thenReturn(Optional.of(version));
    var actual = parserService.getProtected(id, "");
    assertEquals(expected, actual);
  }

  @Test
  public void getProtected_not_found() {
    var id = mock(PackageId.class);
    when(versionRepository.getIfCanAccess(any(PackageId.class), nullable(String.class), anyString()))
      .thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.getProtected(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void getProtected_forbidden() {
    var version = mock(ParserVersion.class);
    var id = mock(PackageId.class);
    when(version.getVisibility()).thenReturn(PackageVisibility.PRIVATE);
    when(versionRepository.getIfCanAccess(any(PackageId.class), nullable(String.class), anyString()))
      .thenReturn(Optional.of(version));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.getProtected(id, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void getPrivate_ok() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(info.getAuthor()).thenReturn(author);
    when(version.getInfo()).thenReturn(info);
    var id = mock(PackageId.class);
    when(id.author()).thenReturn("author");
    when(author.getPseudonym()).thenReturn("author");
    var expected = parserConverter.toPrivateDTO(version);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(author));
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), nullable(String.class)))
      .thenReturn(Optional.of(version));
    var actual = parserService.getPrivate(id, "");
    assertEquals(expected, actual);
  }

  @Test
  public void getPrivate_not_found() {
    var author = mock(User.class);
    var id = mock(PackageId.class);
    when(id.author()).thenReturn("author");
    when(author.getPseudonym()).thenReturn("author");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(author));
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), nullable(String.class)))
      .thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.getPrivate(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void getPrivate_forbidden() {
    var author = mock(User.class);
    var id = mock(PackageId.class);
    when(id.author()).thenReturn("author");
    when(author.getPseudonym()).thenReturn("other user");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(author));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.getPrivate(id, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void getEditable_ok() {
    var author = mock(User.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    when(version.getInfo()).thenReturn(info);
    var id = mock(PackageId.class);
    when(id.author()).thenReturn("author");
    when(author.getPseudonym()).thenReturn("author");
    var expected = parserConverter.toEditDTO(version);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(author));
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), nullable(String.class)))
      .thenReturn(Optional.of(version));
    var actual = parserService.getEditable(id, "");
    assertEquals(expected, actual);
  }

  @Test
  public void getEditable_not_found() {
    var author = mock(User.class);
    var id = mock(PackageId.class);
    when(id.author()).thenReturn("author");
    when(author.getPseudonym()).thenReturn("author");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(author));
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), nullable(String.class)))
      .thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.getEditable(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void getEditable_forbidden() {
    var author = mock(User.class);
    var id = mock(PackageId.class);
    when(id.author()).thenReturn("author");
    when(author.getPseudonym()).thenReturn("other user");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(author));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.getEditable(id, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void create_ok() {
    var form = new PackageCreateForm("name", null, "123");
    var file = mock(PackageFile.class);
    var id = new PackageId(user.getPseudonym(), form.name(), 0);
    var expected = new CreatedDTO(id.toString());
    when(compressionService.createFile()).thenReturn(file);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.empty());
    var actual = parserService.create(form, "");
    assertEquals(expected, actual);
  }

  @Test
  public void create_already_exists() {
    var form = new PackageCreateForm("name", null, "123");
    var info = mock(Parser.class);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.of(info));
    var ex = assertThrows(ConstraintException.class, () -> parserService.create(form, ""));
    assertEquals(ex.getMessage(), PARSER_ALREADY_EXISTS.getMessage());
  }

  @Test
  public void copy_forbidden() {
    var form = new PackageCreateForm("name", "user:from@0.0.1", "123");
    when(passwordEncoder.encode(anyString())).thenReturn("");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.empty());
    when(versionRepository.canAccess(any(PackageId.class), anyString(), anyString())).thenReturn(false);
    var ex = assertThrows(ForbiddenException.class, () -> parserService.create(form, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void copy_not_found() {
    var form = new PackageCreateForm("name", "user:from@0.0.1", "123");
    when(passwordEncoder.encode(anyString())).thenReturn("");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.empty());
    when(versionRepository.canAccess(any(PackageId.class), anyString(), anyString())).thenReturn(true);
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.create(form, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void copy_ok() {
    var file = mock(PackageFile.class);
    var version = mock(ParserVersion.class);
    var form = new PackageCreateForm("name", "user:from@0.0.1", "123");
    var id = new PackageId(user.getPseudonym(), form.name(), 0);
    var expected = new CreatedDTO(id.toString());
    when(version.getParserFile()).thenReturn(file);
    when(version.getBuilderFile()).thenReturn(file);
    when(version.getDocFile()).thenReturn(file);
    when(passwordEncoder.encode(anyString())).thenReturn("");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.empty());
    when(versionRepository.canAccess(any(PackageId.class), anyString(), anyString())).thenReturn(true);
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    var actual = parserService.create(form, "");
    assertEquals(expected, actual);
  }

  @Test
  public void merge_already_exists() {
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    var form = new PackageCreateForm("name", "user:from@0.0.1", "123");
    when(passwordEncoder.encode(anyString())).thenReturn("");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.of(info));
    when(versionRepository.canAccess(any(PackageId.class), anyString(), anyString())).thenReturn(true);
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    when(versionRepository.existsByNameAndVersion(anyString(), anyString(), anyLong())).thenReturn(true);
    var ex = assertThrows(ConstraintException.class, () -> parserService.create(form, ""));
    assertEquals(ex.getMessage(), PARSER_ALREADY_EXISTS.getMessage());
  }

  @Test
  public void merge_ok() {
    var file = mock(PackageFile.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    var form = new PackageCreateForm("name", "user:from@0.0.1", "123");
    var id = new PackageId(user.getPseudonym(), form.name(), 0);
    var expected = new CreatedDTO(id.toString());
    when(version.getParserFile()).thenReturn(file);
    when(version.getBuilderFile()).thenReturn(file);
    when(version.getDocFile()).thenReturn(file);
    when(passwordEncoder.encode(anyString())).thenReturn("");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(parserRepository.getByNameAndEmail(anyString(), anyString())).thenReturn(Optional.of(info));
    when(versionRepository.canAccess(any(PackageId.class), anyString(), anyString())).thenReturn(true);
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    when(versionRepository.existsByNameAndVersion(anyString(), anyString(), anyLong())).thenReturn(false);
    var actual = parserService.create(form, "");
    assertEquals(expected, actual);
  }

  @Test
  public void update_forbidden() {
    var form = new ParserInfoForm("name", "", null, null, null);
    var id = new PackageId("other user", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.update(id, form, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void update_not_found() {
    var form = new ParserInfoForm("name", "", null, null, null);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.update(id, form, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void update_already_exists() {
    var version = mock(ParserVersion.class);
    var form = new ParserInfoForm("name", "", null, null, null);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    when(parserRepository.existsByNameAndEmail(anyString(), anyString())).thenReturn(true);
    var ex = assertThrows(ConstraintException.class, () -> parserService.update(id, form, ""));
    assertEquals(ex.getMessage(), PARSER_ALREADY_EXISTS.getMessage());
  }

  @Test
  public void update_ok() {
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    var form = new ParserInfoForm("name", "", null, null, null);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(version.getInfo()).thenReturn(info);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    when(parserRepository.existsByNameAndEmail(anyString(), anyString())).thenReturn(false);
    parserService.update(id, form, "");
  }

  @Test
  public void delete_forbidden() {
    var id = new PackageId("other user", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.delete(id, false, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void delete_multiple_ok() {
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(version.getInfo()).thenReturn(info);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    parserService.delete(id, false, "");
  }

  @Test
  public void delete_not_found() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.delete(id, false, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void delete_ok() {
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(version.getInfo()).thenReturn(info);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    when(versionRepository.remainingVersions(anyString(), anyString())).thenReturn(0L);
    parserService.delete(id, false, "");
  }

  @Test
  public void like_update_ok() {
    var interaction = mock(ParserInteraction.class);
    var id = new PackageId("author", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.of(interaction));
    parserService.like(id, true, "");
  }

  @Test
  public void like_create_ok() {
    var info = mock(Parser.class);
    var id = new PackageId("author", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.empty());
    when(parserRepository.getByNameAndAuthor(anyString(), anyString())).thenReturn(Optional.of(info));
    parserService.like(id, true, "");
  }

  @Test
  public void like_forbidden() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.like(id, true, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void like_not_found() {
    var id = new PackageId("author", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.empty());
    when(parserRepository.getByNameAndAuthor(anyString(), anyString())).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.like(id, true, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void bookmark_update_ok() {
    var interaction = mock(ParserInteraction.class);
    var id = new PackageId("author", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.of(interaction));
    parserService.bookmark(id, true, "");
  }

  @Test
  public void bookmark_create_ok() {
    var info = mock(Parser.class);
    var id = new PackageId("author", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.empty());
    when(parserRepository.getByNameAndAuthor(anyString(), anyString())).thenReturn(Optional.of(info));
    parserService.bookmark(id, true, "");
  }

  @Test
  public void bookmark_forbidden() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.bookmark(id, true, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void bookmark_not_found() {
    var id = new PackageId("author", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(interactionRepository.getByPackageId(any(PackageId.class), anyString()))
      .thenReturn(Optional.empty());
    when(parserRepository.getByNameAndAuthor(anyString(), anyString())).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.bookmark(id, true, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void createVersion_forbidden() {
    var id = new PackageId("other user", "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    var ex = assertThrows(ForbiddenException.class, () -> parserService.createMajorVersion(id, ""));
    assertEquals(ex.getMessage(), FORBIDDEN.getMessage());
  }

  @Test
  public void createVersion_max_not_found() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    var ex = assertThrows(NotFoundException.class, () -> parserService.createMajorVersion(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void createVersion_parser_not_found() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.createMajorVersion(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void createVersion_no_remaining_version() {
    var id = new PackageId(user.getPseudonym(), "parser", 0xffffffffL);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getMaxVersion(any(PackageId.class))).thenReturn(id.version());
    var ex = assertThrows(ConstraintException.class, () -> parserService.createMajorVersion(id, ""));
    assertEquals(ex.getMessage(), NO_REMAINING_VERSION.getMessage());
  }

  @Test
  public void createVersion_ok() {
    var file = mock(PackageFile.class);
    var info = mock(Parser.class);
    var version = mock(ParserVersion.class);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    var expected = new CreatedDTO(new PackageId(id.author(), id.name(), 0x10000L).toString());
    when(version.getInfo()).thenReturn(info);
    when(version.getParserFile()).thenReturn(file);
    when(version.getBuilderFile()).thenReturn(file);
    when(version.getDocFile()).thenReturn(file);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getMaxVersion(any(PackageId.class))).thenReturn(id.version());
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
    var actual = parserService.createMajorVersion(id, "");
    assertEquals(expected, actual);
  }

  @Test
  public void getParserCode_ok() {
    var expected = new byte[0];
    var file = mock(PackageFile.class);
    var version = mock(ParserVersion.class);
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(file.getContent()).thenReturn(expected);
    when(version.getParserFile()).thenReturn(file);
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), anyString()))
      .thenReturn(Optional.of(version));
    var actual = parserService.getParserCode(id, "", "");
    assertEquals(expected, actual);
  }

  @Test
  public void getParserCode_not_found() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(versionRepository.getIfCanAccess(any(PackageId.class), anyString(), anyString()))
      .thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.getParserCode(id, "", ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }

  @Test
  public void updateParserCode_ok() throws IOException {
    var expected = new byte[0];
    var request = mock(HttpServletRequest.class);
    try(var requestInputStream = mock(ServletInputStream.class)) {
      var file = mock(PackageFile.class);
      var version = mock(ParserVersion.class);
      var id = new PackageId(user.getPseudonym(), "parser", 0);
      when(request.getInputStream()).thenReturn(requestInputStream);
      when(version.getParserFile()).thenReturn(file);
      when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
      when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.of(version));
      when(compressionService.readAndValidate(any(InputStream.class), anyLong())).thenReturn(expected);
      parserService.updateParserCode(id, request, "");
    }
  }

  @Test
  public void updateParserCode_not_found() {
    var id = new PackageId(user.getPseudonym(), "parser", 0);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(versionRepository.getByPackageId(any(PackageId.class))).thenReturn(Optional.empty());
    var ex = assertThrows(NotFoundException.class, () -> parserService.createMajorVersion(id, ""));
    assertEquals(ex.getMessage(), PARSER_NOT_FOUND.getMessage());
  }
}
