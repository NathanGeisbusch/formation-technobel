package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.ParserInteraction;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface ParserInteractionRepository
extends JpaRepository<ParserInteraction, ParserInteraction.PK> {
  @Query("""
    SELECT pi FROM ParserInteraction pi
    WHERE pi.parser.author.pseudonym = :#{#id.author()}
    AND pi.parser.name = :#{#id.name()}
    AND pi.user.email = :userEmail
  """)
  Optional<ParserInteraction> getByPackageId(PackageId id, String userEmail);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM ParserInteraction pi
    WHERE EXISTS (
      SELECT 1 FROM pi.parser p
      WHERE p.author.pseudonym = :#{#id.author()}
      AND p.name = :#{#id.name()}
    )
  """)
  void deleteByPackageId(PackageId id);

  @Query("""
    SELECT COUNT(pi) FROM ParserInteraction pi
    WHERE pi.parser.author.pseudonym = :#{#id.author()}
    AND pi.parser.name = :#{#id.name()}
    AND pi.liked = TRUE
  """)
  long likesByPackageId(PackageId id);

  @Query("""
    SELECT COUNT(pi) FROM ParserInteraction pi
    WHERE pi.parser.author.pseudonym = :#{#id.author()}
    AND pi.parser.name = :#{#id.name()}
    AND pi.liked = FALSE
  """)
  long dislikesByPackageId(PackageId id);
}
