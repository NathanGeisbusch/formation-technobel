package be.technobel.parsemaster.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "generator_interaction")
@IdClass(GeneratorInteraction.PK.class)
public class GeneratorInteraction {
  @Id
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "generator_id", nullable = false)
  private Generator generator;

  @Column(name = "liked")
  private Boolean liked;

  @Column(name = "bookmarked", nullable = false)
  private Boolean bookmarked;

  public GeneratorInteraction() {}

  public GeneratorInteraction(
    User user, Generator generator,
    Boolean liked, Boolean bookmarked
  ) {
    this.user = user;
    this.generator = generator;
    this.liked = liked;
    this.bookmarked = bookmarked;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User author) {
    this.user = author;
  }

  public Generator getGenerator() {
    return generator;
  }

  public void setGenerator(Generator generator) {
    this.generator = generator;
  }

  public Boolean getLiked() {
    return liked;
  }

  public void setLiked(Boolean liked) {
    this.liked = liked;
  }

  public Boolean getBookmarked() {
    return bookmarked;
  }

  public void setBookmarked(Boolean bookmarked) {
    this.bookmarked = bookmarked;
  }

  public static class PK implements Serializable {
    private User user;
    private Generator generator;
    public User getUser() {
      return user;
    }
    public void setUser(User author) {
      this.user = author;
    }
    public Generator getGenerator() {
      return generator;
    }
    public void setGenerator(Generator generator) {
      this.generator = generator;
    }
    public PK() {}
    public PK(User user, Generator generator) {
      this.user = user;
      this.generator = generator;
    }
    @Override
    public boolean equals(Object o) {
      if(this == o) return true;
      if(!(o instanceof PK pk)) return false;
      return Objects.equals(getUser().getId(), pk.getUser().getId())
        && Objects.equals(getGenerator().getId(), pk.getGenerator().getId());
    }
    @Override
    public int hashCode() {
      return Objects.hash(getUser().getId(), getGenerator().getId());
    }
  }
}
