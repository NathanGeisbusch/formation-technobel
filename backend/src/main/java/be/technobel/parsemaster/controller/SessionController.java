package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.dto.PageDTO;
import be.technobel.parsemaster.dto.SessionDTO;
import be.technobel.parsemaster.enumeration.SortSession;
import be.technobel.parsemaster.form.SessionCreateForm;
import be.technobel.parsemaster.form.SessionEditForm;
import be.technobel.parsemaster.openapi.SessionApi;
import be.technobel.parsemaster.service.declaration.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController implements SessionApi {
  private final SessionService sessionService;

  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Override
  public PageDTO<SessionDTO> find(
    Integer page, Integer size, String search, SortSession sort, Authentication auth
  ) {
    return this.sessionService.find(page, size, search, sort, auth.getName());
  }

  @Override
  public CreatedDTO create(SessionCreateForm form, Authentication auth) {
    return this.sessionService.create(form, auth.getName());
  }

  @Override
  public SessionDTO get(String id, Authentication auth) {
    return this.sessionService.get(id, auth.getName());
  }

  @Override
  public void update(String id, SessionEditForm form, Authentication auth) {
    this.sessionService.update(id, form, auth.getName());
  }

  @Override
  public void delete(String id, Authentication auth) {
    this.sessionService.delete(id, auth.getName());
  }

  @Override
  public byte[] getInputText(String id, Authentication auth) {
    return this.sessionService.getInputText(id, auth.getName());
  }

  @Override
  public byte[] getParserCode(String id, Authentication auth) {
    return this.sessionService.getParserCode(id, auth.getName());
  }

  @Override
  public byte[] getBuilderCode(String id, Authentication auth) {
    return this.sessionService.getBuilderCode(id, auth.getName());
  }

  @Override
  public byte[] getGeneratorCode(String id, Authentication auth) {
    return this.sessionService.getGeneratorCode(id, auth.getName());
  }

  @Override
  public byte[] getDocCode(String id, Authentication auth) {
    return this.sessionService.getDocCode(id, auth.getName());
  }

  @Override
  public void updateInputText(String id, HttpServletRequest request, Authentication auth) {
    this.sessionService.updateInputText(id, request, auth.getName());
  }
}
