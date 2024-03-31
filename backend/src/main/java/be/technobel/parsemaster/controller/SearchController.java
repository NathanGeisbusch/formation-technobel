package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.openapi.SearchApi;
import be.technobel.parsemaster.service.declaration.SearchService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class SearchController implements SearchApi {
  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @Override
  public boolean existsPseudonym(String value) {
    return searchService.existsPseudonym(value);
  }

  @Override
  public boolean existsEmail(String value) {
    return searchService.existsEmail(value);
  }

  @Override
  public boolean existsParserByName(String name, Authentication auth) {
    return searchService.existsParserByName(auth, name);
  }

  @Override
  public boolean existsParserByVersion(String name, String version, Authentication auth) {
    return searchService.existsParserByVersion(auth, name, version);
  }

  @Override
  public boolean existsGeneratorByName(String name, Authentication auth) {
    return searchService.existsGeneratorByName(auth, name);
  }

  @Override
  public boolean existsGeneratorByVersion(String name, String version, Authentication auth) {
    return searchService.existsGeneratorByVersion(auth, name, version);
  }

  @Override
  public boolean existsSessionByName(String name, Authentication auth) {
    return searchService.existsSession(auth, name);
  }

  @Override
  public List<String> findParserVersions(String author, String name, String value, Authentication auth) {
    return searchService.findParserVersions(auth, author, name, value);
  }

  @Override
  public List<String> findGeneratorVersions(String author, String name, String value, Authentication auth) {
    return searchService.findGeneratorVersions(auth, author, name, value);
  }
}
