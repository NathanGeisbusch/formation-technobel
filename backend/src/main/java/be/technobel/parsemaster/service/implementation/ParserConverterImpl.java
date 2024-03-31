package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.PackagePrivateDTO;
import be.technobel.parsemaster.dto.PackagePublicDTO;
import be.technobel.parsemaster.dto.ParserEditDTO;
import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.form.ParserInfoForm;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.declaration.ParserConverter;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ParserConverterImpl implements ParserConverter {
  private final CompressionService compressionService;

  public ParserConverterImpl(CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Override
  public PackagePublicDTO toPublicDTO(
    ParserVersion version,
    ParserInteraction interaction,
    long likes, long dislikes
  ) {
    final Boolean liked, bookmarked;
    if(interaction == null) { liked = null; bookmarked = false; }
    else { liked = interaction.getLiked(); bookmarked = interaction.getBookmarked(); }
    return new PackagePublicDTO(
      version.getInfo().getName(),
      PackageId.versionToString(version.getVersion()),
      version.getInfo().getAuthor().getPseudonym(),
      version.getDescription(),
      version.getUpdatedAt(),
      likes, dislikes, liked, bookmarked,
      version.getSyntax(),
      null
    );
  }

  @Override
  public PackagePrivateDTO toPrivateDTO(ParserVersion version) {
    return new PackagePrivateDTO(
      version.getInfo().getName(),
      PackageId.versionToString(version.getVersion()),
      version.getInfo().getAuthor().getPseudonym(),
      version.getDescription(),
      version.getUpdatedAt(),
      version.getVisibility(),
      version.getSyntax(),
      null
    );
  }

  @Override
  public ParserEditDTO toEditDTO(ParserVersion version) {
    return new ParserEditDTO(
      version.getInfo().getName(),
      PackageId.versionToString(version.getVersion()),
      version.getDescription(),
      version.getSyntax(),
      version.getVisibility(),
      version.getPassword()
    );
  }

  @Override
  public ParserVersion create(User author, String name) {
    return new ParserVersion(
      new Parser(name, author),
      compressionService.createFile(),
      compressionService.createFile(),
      compressionService.createFile()
    );
  }

  @Override
  public ParserVersion copy(User author, String name, ParserVersion origin) {
    final var result = new ParserVersion(
      new Parser(name, author),
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setSyntax(origin.getSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    return result;
  }

  @Override
  public ParserVersion createRevision(User author, String name, ParserVersion origin, Long version) {
    final var result = new ParserVersion(
      origin.getInfo(),
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setSyntax(origin.getSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    result.setVersion(version);
    return result;
  }

  @Override
  public ParserVersion merge(Parser existing, ParserVersion origin) {
    final var result = new ParserVersion(
      existing,
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setSyntax(origin.getSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    result.setVersion(origin.getVersion());
    return result;
  }

  @Override
  public void update(ParserVersion version, ParserInfoForm form) {
    if(form.name() != null) version.getInfo().setName(form.name());
    if(form.description() != null) version.setDescription(form.description());
    if(form.syntax() != null) version.setSyntax(form.syntax());
    if(form.visibility() != null) {
      version.setVisibility(form.visibility());
      if(form.visibility() == PackageVisibility.PROTECTED && form.password() != null) {
        version.setPassword(form.password());
      }
    }
    version.setUpdatedAt(LocalDateTime.now());
  }

  private PackageFile copy(PackageFile origin) {
    final var result = new PackageFile();
    result.setEncoding(origin.getEncoding());
    result.setContent(origin.getContent());
    return result;
  }
}
