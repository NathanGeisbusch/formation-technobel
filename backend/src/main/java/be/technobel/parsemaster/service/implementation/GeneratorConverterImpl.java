package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.PackagePrivateDTO;
import be.technobel.parsemaster.dto.PackagePublicDTO;
import be.technobel.parsemaster.dto.GeneratorEditDTO;
import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.form.GeneratorInfoForm;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.declaration.GeneratorConverter;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GeneratorConverterImpl implements GeneratorConverter {
  private final CompressionService compressionService;

  public GeneratorConverterImpl(CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  @Override
  public PackagePublicDTO toPublicDTO(
    GeneratorVersion version,
    GeneratorInteraction interaction,
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
      version.getParserSyntax(),
      version.getGeneratorSyntax()
    );
  }

  @Override
  public PackagePrivateDTO toPrivateDTO(GeneratorVersion version) {
    return new PackagePrivateDTO(
      version.getInfo().getName(),
      PackageId.versionToString(version.getVersion()),
      version.getInfo().getAuthor().getPseudonym(),
      version.getDescription(),
      version.getUpdatedAt(),
      version.getVisibility(),
      version.getParserSyntax(),
      version.getGeneratorSyntax()
    );
  }

  @Override
  public GeneratorEditDTO toEditDTO(GeneratorVersion version) {
    return new GeneratorEditDTO(
      version.getInfo().getName(),
      PackageId.versionToString(version.getVersion()),
      version.getDescription(),
      version.getParserSyntax(),
      version.getGeneratorSyntax(),
      version.getVisibility(),
      version.getPassword()
    );
  }

  @Override
  public GeneratorVersion create(User author, String name) {
    return new GeneratorVersion(
      new Generator(name, author),
      compressionService.createFile(),
      compressionService.createFile(),
      compressionService.createFile(),
      compressionService.createFile()
    );
  }

  @Override
  public GeneratorVersion copy(User author, String name, GeneratorVersion origin) {
    final var result = new GeneratorVersion(
      new Generator(name, author),
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      copy(origin.getGeneratorFile()),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setParserSyntax(origin.getParserSyntax());
    result.setGeneratorSyntax(origin.getGeneratorSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    return result;
  }

  @Override
  public GeneratorVersion createRevision(User author, String name, GeneratorVersion origin, Long version) {
    final var result = new GeneratorVersion(
      origin.getInfo(),
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      copy(origin.getGeneratorFile()),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setParserSyntax(origin.getParserSyntax());
    result.setGeneratorSyntax(origin.getGeneratorSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    result.setVersion(version);
    return result;
  }

  @Override
  public GeneratorVersion copy(User author, String name, ParserVersion origin) {
    final var result = new GeneratorVersion(
      new Generator(name, author),
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      compressionService.createFile(),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setParserSyntax(origin.getSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    return result;
  }

  @Override
  public GeneratorVersion merge(Generator existing, GeneratorVersion origin) {
    final var result = new GeneratorVersion(
      existing,
      copy(origin.getParserFile()),
      copy(origin.getBuilderFile()),
      copy(origin.getGeneratorFile()),
      copy(origin.getDocFile())
    );
    result.setDescription(origin.getDescription());
    result.setParserSyntax(origin.getParserSyntax());
    result.setGeneratorSyntax(origin.getGeneratorSyntax());
    result.setDocSyntax(origin.getDocSyntax());
    result.setVersion(origin.getVersion());
    return result;
  }

  @Override
  public void update(GeneratorVersion version, GeneratorInfoForm form) {
    if(form.name() != null) version.getInfo().setName(form.name());
    if(form.description() != null) version.setDescription(form.description());
    if(form.parserSyntax() != null) version.setParserSyntax(form.parserSyntax());
    if(form.generatorSyntax() != null) version.setGeneratorSyntax(form.generatorSyntax());
    if(form.visibility() != null) {
      version.setVisibility(form.visibility());
      if(form.visibility() == PackageVisibility.PROTECTED && form.password() != null) {
        version.setPassword(form.password());
      }
    }
    else if(version.getVisibility() == PackageVisibility.PROTECTED && form.password() != null) {
      version.setPassword(form.password());
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
