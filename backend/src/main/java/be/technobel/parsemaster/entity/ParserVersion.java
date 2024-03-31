package be.technobel.parsemaster.entity;

import be.technobel.parsemaster.enumeration.DocumentationSyntax;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.ParserSyntax;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parser_version")
public class ParserVersion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parser_id", nullable = false)
  private Parser info;

  @Column(name = "version", nullable = false)
  private Long version = 0L;

  @Column(name = "description", nullable = false)
  private String description = "";

  @Column(name = "syntax", nullable = false)
  @Enumerated(value = EnumType.ORDINAL)
  private ParserSyntax syntax = ParserSyntax.PM_PARSER_0_0_1;

  @Column(name = "doc_syntax", nullable = false)
  @Enumerated(value = EnumType.ORDINAL)
  private DocumentationSyntax docSyntax = DocumentationSyntax.MARKDOWN;

  @Column(name = "visibility", nullable = false)
  @Enumerated(value = EnumType.ORDINAL)
  private PackageVisibility visibility = PackageVisibility.PRIVATE;

  @Column(name = "protected_password")
  private String password = "";

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "parser_file", nullable = false)
  private PackageFile parserFile;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "builder_file", nullable = false)
  private PackageFile builderFile;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "doc_file", nullable = false)
  private PackageFile docFile;

  public ParserVersion() {}

  public ParserVersion(Parser info, PackageFile parserFile, PackageFile builderFile, PackageFile docFile) {
    this.info = info;
    this.parserFile = parserFile;
    this.builderFile = builderFile;
    this.docFile = docFile;
    this.updatedAt = this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Parser getInfo() {
    return info;
  }

  public void setInfo(Parser info) {
    this.info = info;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ParserSyntax getSyntax() {
    return syntax;
  }

  public void setSyntax(ParserSyntax syntax) {
    this.syntax = syntax;
  }

  public DocumentationSyntax getDocSyntax() {
    return docSyntax;
  }

  public void setDocSyntax(DocumentationSyntax docFormat) {
    this.docSyntax = docFormat;
  }

  public PackageVisibility getVisibility() {
    return visibility;
  }

  public void setVisibility(PackageVisibility visibility) {
    this.visibility = visibility;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String passwordHash) {
    this.password = passwordHash;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public PackageFile getParserFile() {
    return parserFile;
  }

  public void setParserFile(PackageFile parserFile) {
    this.parserFile = parserFile;
  }

  public PackageFile getBuilderFile() {
    return builderFile;
  }

  public void setBuilderFile(PackageFile builderFile) {
    this.builderFile = builderFile;
  }

  public PackageFile getDocFile() {
    return docFile;
  }

  public void setDocFile(PackageFile docFile) {
    this.docFile = docFile;
  }
}
