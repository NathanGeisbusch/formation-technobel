package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.form.PackagesDeleteForm;
import be.technobel.parsemaster.form.SessionsDeleteForm;
import be.technobel.parsemaster.openapi.BulkDeleteApi;
import be.technobel.parsemaster.service.declaration.GeneratorService;
import be.technobel.parsemaster.service.declaration.ParserService;
import be.technobel.parsemaster.service.declaration.SessionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BulkDeleteController implements BulkDeleteApi {
  private final ParserService parserService;
  private final GeneratorService generatorService;
  private final SessionService sessionService;

  public BulkDeleteController(
    ParserService parserService,
    GeneratorService generatorService,
    SessionService sessionService
  ) {
    this.parserService = parserService;
    this.generatorService = generatorService;
    this.sessionService = sessionService;
  }

  @Override
  public void deleteParsers(PackagesDeleteForm form, Boolean allVersions, Authentication auth) {
    this.parserService.delete(form, allVersions, auth.getName());
  }

  @Override
  public void deleteGenerators(PackagesDeleteForm form, Boolean allVersions, Authentication auth) {
    this.generatorService.delete(form, allVersions, auth.getName());
  }

  @Override
  public void deleteSessions(SessionsDeleteForm form, Authentication auth) {
    this.sessionService.delete(form, auth.getName());
  }
}
