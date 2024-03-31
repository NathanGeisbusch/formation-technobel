package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.PackagesDeleteForm;
import be.technobel.parsemaster.form.GeneratorInfoForm;
import be.technobel.parsemaster.util.PackageId;
import jakarta.servlet.http.HttpServletRequest;

public interface GeneratorService {
  PageDTO<PackagePublicDTO> findPublic(int page, int size, String search, SortPublicPackage sort, String userEmail);

  PageDTO<PackagePublicDTO> findBookmarked(int page, int size, String search, SortPublicPackage sort, String userEmail);

  PageDTO<PackagePrivateDTO> findOwn(int page, int size, String search, Boolean allVersions, PackageVisibility visibility, SortPrivatePackage sort, String userEmail);

  PackagePublicDTO getPublic(PackageId id, String userEmail);

  PackagePrivateDTO getProtected(PackageId id, String password);

  PackagePrivateDTO getPrivate(PackageId id, String userEmail);

  GeneratorEditDTO getEditable(PackageId id, String userEmail);

  CreatedDTO create(PackageCreateForm form, Boolean fromParser, String userEmail);

  void update(PackageId id, GeneratorInfoForm form, String userEmail);

  void delete(PackageId id, Boolean allVersions, String userEmail);

  void delete(PackagesDeleteForm form, Boolean allVersions, String userEmail);

  void like(PackageId id, Boolean value, String userEmail);

  void bookmark(PackageId id, Boolean value, String userEmail);

  CreatedDTO createMajorVersion(PackageId id, String userEmail);

  CreatedDTO createMinorVersion(PackageId id, String userEmail);

  CreatedDTO createPatchVersion(PackageId id, String userEmail);

  byte[] getParserCode(PackageId id, String password, String userEmail);

  byte[] getBuilderCode(PackageId id, String password, String userEmail);

  byte[] getGeneratorCode(PackageId id, String password, String userEmail);

  byte[] getDocCode(PackageId id, String password, String userEmail);

  void updateParserCode(PackageId id, HttpServletRequest request, String userEmail);

  void updateBuilderCode(PackageId id, HttpServletRequest request, String userEmail);

  void updateGeneratorCode(PackageId id, HttpServletRequest request, String userEmail);

  void updateDocCode(PackageId id, HttpServletRequest request, String userEmail);
}
