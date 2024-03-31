package be.technobel.parsemaster;

import be.technobel.parsemaster.entity.User;
import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.implementation.SearchServiceImpl;
import be.technobel.parsemaster.util.PackageId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private ParserRepository parserRepository;
  @Mock private ParserVersionRepository parserVersionRepository;
  @Mock private GeneratorRepository generatorRepository;
  @Mock private GeneratorVersionRepository generatorVersionRepository;
  @Mock private SessionRepository sessionRepository;
  @InjectMocks private SearchServiceImpl searchService;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("user", "user@test.be", "123");
  }

  @Test
  public void existsPseudonym_true() {
    when(userRepository.existsByPseudonym(anyString())).thenReturn(true);
    var actual = searchService.existsPseudonym(user.getPseudonym());
    assertTrue(actual);
  }

  @Test
  public void existsPseudonym_false() {
    when(userRepository.existsByPseudonym(anyString())).thenReturn(false);
    var actual = searchService.existsPseudonym(user.getPseudonym());
    assertFalse(actual);
  }

  @Test
  public void existsEmail_true() {
    when(userRepository.existsByEmail(anyString())).thenReturn(true);
    var actual = searchService.existsEmail(user.getPseudonym());
    assertTrue(actual);
  }

  @Test
  public void existsEmail_false() {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    var actual = searchService.existsEmail(user.getPseudonym());
    assertFalse(actual);
  }

  @Test
  public void existsParserByName_true() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(parserRepository.existsByNameAndEmail(anyString(), anyString())).thenReturn(true);
    var actual = searchService.existsParserByName(auth, "parser");
    assertTrue(actual);
  }

  @Test
  public void existsParserByName_false() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(parserRepository.existsByNameAndEmail(anyString(), anyString())).thenReturn(false);
    var actual = searchService.existsParserByName(auth, "parser");
    assertFalse(actual);
  }

  @Test
  public void existsGeneratorByName_true() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(generatorRepository.existsByNameAndEmail(anyString(), anyString())).thenReturn(true);
    var actual = searchService.existsGeneratorByName(auth, "generator");
    assertTrue(actual);
  }

  @Test
  public void existsGeneratorByName_false() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(generatorRepository.existsByNameAndEmail(anyString(), anyString())).thenReturn(false);
    var actual = searchService.existsGeneratorByName(auth, "generator");
    assertFalse(actual);
  }

  @Test
  public void existsSession_true() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(sessionRepository.existsByName(anyString(), anyString())).thenReturn(true);
    var actual = searchService.existsSession(auth, "session");
    assertTrue(actual);
  }

  @Test
  public void existsSession_false() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(sessionRepository.existsByName(anyString(), anyString())).thenReturn(false);
    var actual = searchService.existsSession(auth, "session");
    assertFalse(actual);
  }

  @Test
  public void existsParserByVersion_true() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(parserVersionRepository.existsByNameAndVersion(anyString(), anyString(), anyLong())).thenReturn(true);
    var actual = searchService.existsParserByVersion(auth, "parser", "0.0.1");
    assertTrue(actual);
  }

  @Test
  public void existsParserByVersion_false() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(parserVersionRepository.existsByNameAndVersion(anyString(), anyString(), anyLong())).thenReturn(false);
    var actual = searchService.existsParserByVersion(auth, "parser", "0.0.1");
    assertFalse(actual);
  }

  @Test
  public void existsParserByVersion_bad_version() {
    var auth = mock(Authentication.class);
    var actual = searchService.existsParserByVersion(auth, "parser", "0.0.");
    assertFalse(actual);
  }

  @Test
  public void existsGeneratorByVersion_true() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(generatorVersionRepository.existsByNameAndVersion(anyString(), anyString(), anyLong())).thenReturn(true);
    var actual = searchService.existsGeneratorByVersion(auth, "generator", "0.0.1");
    assertTrue(actual);
  }

  @Test
  public void existsGeneratorByVersion_false() {
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(generatorVersionRepository.existsByNameAndVersion(anyString(), anyString(), anyLong())).thenReturn(false);
    var actual = searchService.existsGeneratorByVersion(auth, "generator", "0.0.1");
    assertFalse(actual);
  }

  @Test
  public void existsGeneratorByVersion_bad_version() {
    var auth = mock(Authentication.class);
    var actual = searchService.existsGeneratorByVersion(auth, "generator", "0.0.");
    assertFalse(actual);
  }

  @Test
  public void findParserVersions_ok() {
    var returns = List.of(1L, 2L, 3L);
    var expected = returns.stream().map(PackageId::versionToString).toList();
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(auth.isAuthenticated()).thenReturn(true);
    when(parserVersionRepository.findVersions(
      any(Pageable.class), anyString(), anyString(), anyString(), anyLong(), anyLong())
    ).thenReturn(new PageImpl<>(returns));
    var actual = searchService.findParserVersions(
      auth, user.getPseudonym(), "parser", "0.0.1"
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findParserVersions_bad_range() {
    List<String> expected = List.of();
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(auth.isAuthenticated()).thenReturn(true);
    var actual = searchService.findParserVersions(
      auth, user.getPseudonym(), "parser", "0.999"
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findParserVersions_not_auth() {
    var returns = List.of(1L, 2L, 3L);
    var expected = returns.stream().map(PackageId::versionToString).toList();
    var auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(false);
    when(parserVersionRepository.findVersions(
      any(Pageable.class), nullable(String.class), anyString(), anyString(), anyLong(), anyLong())
    ).thenReturn(new PageImpl<>(returns));
    var actual = searchService.findParserVersions(
      auth, user.getPseudonym(), "parser", "0.0.1"
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findGeneratorVersions_ok() {
    var returns = List.of(1L, 2L, 3L);
    var expected = returns.stream().map(PackageId::versionToString).toList();
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(auth.isAuthenticated()).thenReturn(true);
    when(generatorVersionRepository.findVersions(
      any(Pageable.class), anyString(), anyString(), anyString(), anyLong(), anyLong())
    ).thenReturn(new PageImpl<>(returns));
    var actual = searchService.findGeneratorVersions(
      auth, user.getPseudonym(), "generator", "0.0.1"
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findGeneratorVersions_bad_range() {
    List<String> expected = List.of();
    var auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(auth.isAuthenticated()).thenReturn(true);
    var actual = searchService.findGeneratorVersions(
      auth, user.getPseudonym(), "generator", "0.999"
    );
    assertEquals(expected, actual);
  }

  @Test
  public void findGeneratorVersions_not_auth() {
    var returns = List.of(1L, 2L, 3L);
    var expected = returns.stream().map(PackageId::versionToString).toList();
    var auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(false);
    when(generatorVersionRepository.findVersions(
      any(Pageable.class), nullable(String.class), anyString(), anyString(), anyLong(), anyLong())
    ).thenReturn(new PageImpl<>(returns));
    var actual = searchService.findGeneratorVersions(
      auth, user.getPseudonym(), "generator", "0.0.1"
    );
    assertEquals(expected, actual);
  }
}
