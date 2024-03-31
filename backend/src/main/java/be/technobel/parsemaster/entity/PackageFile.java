package be.technobel.parsemaster.entity;

import be.technobel.parsemaster.enumeration.CompressionEncoding;
import jakarta.persistence.*;

@Entity
@Table(name = "package_file")
public class PackageFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "content", nullable = false, columnDefinition = "bytea")
  private byte[] content;

  @Column(name = "encoding", nullable = false)
  private CompressionEncoding encoding = CompressionEncoding.GZIP;

  public PackageFile() {}

  public PackageFile(byte[] content) {
    this.content = content;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public CompressionEncoding getEncoding() {
    return encoding;
  }

  public void setEncoding(CompressionEncoding encoding) {
    this.encoding = encoding;
  }
}
