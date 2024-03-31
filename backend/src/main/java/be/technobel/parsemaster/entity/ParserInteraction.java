package be.technobel.parsemaster.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "parser_interaction")
@IdClass(ParserInteraction.PK.class)
public class ParserInteraction {
  @Id
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "parser_id", nullable = false)
  private Parser parser;

  @Column(name = "liked")
  private Boolean liked;

  @Column(name = "bookmarked", nullable = false)
  private Boolean bookmarked;

  public ParserInteraction() {}

  public ParserInteraction(
    User user, Parser parser,
    Boolean liked, Boolean bookmarked
  ) {
    this.user = user;
    this.parser = parser;
    this.liked = liked;
    this.bookmarked = bookmarked;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User author) {
    this.user = author;
  }

  public Parser getParser() {
    return parser;
  }

  public void setParser(Parser parserVersion) {
    this.parser = parserVersion;
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
    private Parser parser;
    public User getUser() {
      return user;
    }
    public void setUser(User author) {
      this.user = author;
    }
    public Parser getParser() {
      return parser;
    }
    public void setParser(Parser parserVersion) {
      this.parser = parserVersion;
    }
    public PK() {}
    public PK(User user, Parser parser) {
      this.user = user;
      this.parser = parser;
    }
    @Override
    public boolean equals(Object o) {
      if(this == o) return true;
      if(!(o instanceof PK pk)) return false;
      return Objects.equals(getUser().getId(), pk.getUser().getId())
        && Objects.equals(getParser().getId(), pk.getParser().getId());
    }
    @Override
    public int hashCode() {
      return Objects.hash(getUser().getId(), getParser().getId());
    }
  }
}
