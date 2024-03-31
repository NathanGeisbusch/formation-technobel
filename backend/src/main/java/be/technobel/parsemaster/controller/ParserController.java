package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.ParserInfoForm;
import be.technobel.parsemaster.openapi.ParserApi;
import be.technobel.parsemaster.service.declaration.ParserService;
import be.technobel.parsemaster.util.PackageId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParserController implements ParserApi {
  private final ParserService parserService;

  public ParserController(ParserService parserService) {
    this.parserService = parserService;
  }

  @Override
  public PageDTO<PackagePublicDTO> findPublic(
    Integer page, Integer size, String search,
    SortPublicPackage sort, Authentication auth
  ) {
    final var userEmail = auth == null ? null : auth.getName();
    return this.parserService.findPublic(page, size, search, sort, userEmail);
  }

  @Override
  public PageDTO<PackagePublicDTO> findBookmarked(
    Integer page, Integer size, String search,
    SortPublicPackage sort, Authentication auth
  ) {
    return this.parserService.findBookmarked(page, size, search, sort, auth.getName());
  }

  @Override
  public PageDTO<PackagePrivateDTO> findOwn(
    Integer page, Integer size, String search, Boolean allVersions,
    PackageVisibility visibility, SortPrivatePackage sort, Authentication auth
  ) {
    return this.parserService.findOwn(page, size, search, allVersions, visibility, sort, auth.getName());
  }

  @Override
  public CreatedDTO create(PackageCreateForm form, Authentication auth) {
    return this.parserService.create(form, auth.getName());
  }

  @Override
  public PackagePublicDTO getPublic(String id, Authentication auth) {
    final var userEmail = auth == null ? null : auth.getName();
    return this.parserService.getPublic(PackageId.from(id), userEmail);
  }

  @Override
  public PackagePrivateDTO getProtected(String id, String password) {
    return this.parserService.getProtected(PackageId.from(id), password);
  }

  @Override
  public PackagePrivateDTO getPrivate(String id, Authentication auth) {
    return this.parserService.getPrivate(PackageId.from(id), auth.getName());
  }

  @Override
  public ParserEditDTO getEditable(String id, Authentication auth) {
    return this.parserService.getEditable(PackageId.from(id), auth.getName());
  }

  @Override
  public void update(String id, ParserInfoForm form, Authentication auth) {
    this.parserService.update(PackageId.from(id), form, auth.getName());
  }

  @Override
  public void delete(String id, Boolean allVersions, Authentication auth) {
    this.parserService.delete(PackageId.from(id), allVersions, auth.getName());
  }

  @Override
  public void like(String id, Boolean value, Authentication auth) {
    this.parserService.like(PackageId.from(id), value, auth.getName());
  }

  @Override
  public void bookmark(String id, Boolean value, Authentication auth) {
    this.parserService.bookmark(PackageId.from(id), value, auth.getName());
  }

  @Override
  public CreatedDTO createMajorVersion(String id, Authentication auth) {
    return this.parserService.createMajorVersion(PackageId.from(id), auth.getName());
  }

  @Override
  public CreatedDTO createMinorVersion(String id, Authentication auth) {
    return this.parserService.createMinorVersion(PackageId.from(id), auth.getName());
  }

  @Override
  public CreatedDTO createPatchVersion(String id, Authentication auth) {
    return this.parserService.createPatchVersion(PackageId.from(id), auth.getName());
  }

  @Override
  public byte[] getParserCode(String id, String password, Authentication auth) {
    return this.parserService.getParserCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public byte[] getBuilderCode(String id, String password, Authentication auth) {
    return this.parserService.getBuilderCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public byte[] getDocCode(String id, String password, Authentication auth) {
    return this.parserService.getDocCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public void updateParserCode(String id, HttpServletRequest request, Authentication auth) {
    this.parserService.updateParserCode(PackageId.from(id), request, auth.getName());
  }

  @Override
  public void updateBuilderCode(String id, HttpServletRequest request, Authentication auth) {
    this.parserService.updateBuilderCode(PackageId.from(id), request, auth.getName());
  }

  @Override
  public void updateDocCode(String id, HttpServletRequest request, Authentication auth) {
    this.parserService.updateDocCode(PackageId.from(id), request, auth.getName());
  }
}
