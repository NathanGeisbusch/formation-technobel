package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.GeneratorInfoForm;
import be.technobel.parsemaster.openapi.GeneratorApi;
import be.technobel.parsemaster.service.declaration.GeneratorService;
import be.technobel.parsemaster.util.PackageId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneratorController implements GeneratorApi {
  private final GeneratorService generatorService;

  public GeneratorController(GeneratorService generatorService) {
    this.generatorService = generatorService;
  }

  @Override
  public PageDTO<PackagePublicDTO> findPublic(
    Integer page, Integer size, String search,
    SortPublicPackage sort, Authentication auth
  ) {
    final var userEmail = auth == null ? null : auth.getName();
    return this.generatorService.findPublic(page, size, search, sort, userEmail);
  }

  @Override
  public PageDTO<PackagePublicDTO> findBookmarked(
    Integer page, Integer size, String search,
    SortPublicPackage sort, Authentication auth
  ) {
    return this.generatorService.findBookmarked(page, size, search, sort, auth.getName());
  }

  @Override
  public PageDTO<PackagePrivateDTO> findOwn(
    Integer page, Integer size, String search, Boolean allVersions,
    PackageVisibility visibility, SortPrivatePackage sort, Authentication auth
  ) {
    return this.generatorService.findOwn(page, size, search, allVersions, visibility, sort, auth.getName());
  }

  @Override
  public CreatedDTO create(PackageCreateForm form, Boolean fromParser, Authentication auth) {
    return this.generatorService.create(form, fromParser, auth.getName());
  }

  @Override
  public PackagePublicDTO getPublic(String id, Authentication auth) {
    final var userEmail = auth == null ? null : auth.getName();
    return this.generatorService.getPublic(PackageId.from(id), userEmail);
  }

  @Override
  public PackagePrivateDTO getProtected(String id, String password) {
    return this.generatorService.getProtected(PackageId.from(id), password);
  }

  @Override
  public PackagePrivateDTO getPrivate(String id, Authentication auth) {
    return this.generatorService.getPrivate(PackageId.from(id), auth.getName());
  }

  @Override
  public GeneratorEditDTO getEditable(String id, Authentication auth) {
    return this.generatorService.getEditable(PackageId.from(id), auth.getName());
  }

  @Override
  public void update(String id, GeneratorInfoForm form, Authentication auth) {
    this.generatorService.update(PackageId.from(id), form, auth.getName());
  }

  @Override
  public void delete(String id, Boolean allVersions, Authentication auth) {
    this.generatorService.delete(PackageId.from(id), allVersions, auth.getName());
  }

  @Override
  public void like(String id, Boolean value, Authentication auth) {
    this.generatorService.like(PackageId.from(id), value, auth.getName());
  }

  @Override
  public void bookmark(String id, Boolean value, Authentication auth) {
    this.generatorService.bookmark(PackageId.from(id), value, auth.getName());
  }

  @Override
  public CreatedDTO createMajorVersion(String id, Authentication auth) {
    return this.generatorService.createMajorVersion(PackageId.from(id), auth.getName());
  }

  @Override
  public CreatedDTO createMinorVersion(String id, Authentication auth) {
    return this.generatorService.createMinorVersion(PackageId.from(id), auth.getName());
  }

  @Override
  public CreatedDTO createPatchVersion(String id, Authentication auth) {
    return this.generatorService.createPatchVersion(PackageId.from(id), auth.getName());
  }

  @Override
  public byte[] getParserCode(String id, String password, Authentication auth) {
    return this.generatorService.getParserCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public byte[] getBuilderCode(String id, String password, Authentication auth) {
    return this.generatorService.getBuilderCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public byte[] getGeneratorCode(String id, String password, Authentication auth) {
    return this.generatorService.getGeneratorCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public byte[] getDocCode(String id, String password, Authentication auth) {
    return this.generatorService.getDocCode(PackageId.from(id), password, auth.getName());
  }

  @Override
  public void updateParserCode(String id, HttpServletRequest request, Authentication auth) {
    this.generatorService.updateParserCode(PackageId.from(id), request, auth.getName());
  }

  @Override
  public void updateBuilderCode(String id, HttpServletRequest request, Authentication auth) {
    this.generatorService.updateBuilderCode(PackageId.from(id), request, auth.getName());
  }

  @Override
  public void updateGeneratorCode(String id, HttpServletRequest request, Authentication auth) {
    this.generatorService.updateGeneratorCode(PackageId.from(id), request, auth.getName());
  }

  @Override
  public void updateDocCode(String id, HttpServletRequest request, Authentication auth) {
    this.generatorService.updateDocCode(PackageId.from(id), request, auth.getName());
  }
}
