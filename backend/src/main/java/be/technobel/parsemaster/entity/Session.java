package be.technobel.parsemaster.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "generator_id", nullable = false)
  private GeneratorVersion generatorVersion;

  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "input_file", nullable = false)
  private PackageFile inputFile;

  public Session() {}

  public Session(GeneratorVersion generatorVersion, String name, User author, PackageFile inputFile) {
    this.generatorVersion = generatorVersion;
    this.name = name;
    this.author = author;
    this.inputFile = inputFile;
    this.updatedAt = this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GeneratorVersion getGeneratorVersion() {
    return generatorVersion;
  }

  public void setGeneratorVersion(GeneratorVersion generatorVersion) {
    this.generatorVersion = generatorVersion;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
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

  public PackageFile getInputFile() {
    return inputFile;
  }

  public void setInputFile(PackageFile inputFile) {
    this.inputFile = inputFile;
  }
}
