package be.technobel.parsemaster.service.declaration;

import org.springframework.security.core.Authentication;
import java.util.List;

public interface SearchService {
  boolean existsPseudonym(String pseudonym);

  boolean existsEmail(String email);

  boolean existsParserByName(Authentication auth, String name);

  boolean existsGeneratorByName(Authentication auth, String name);

  boolean existsSession(Authentication auth, String name);

  List<String> findParserVersions(Authentication auth, String author, String name, String version);

  List<String> findGeneratorVersions(Authentication auth, String author, String name, String version);

  boolean existsParserByVersion(Authentication auth, String name, String version);

  boolean existsGeneratorByVersion(Authentication auth, String name, String version);
}
