package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.GeneratorInteraction;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface GeneratorInteractionRepository
extends JpaRepository<GeneratorInteraction, GeneratorInteraction.PK> {
  @Query("""
    SELECT gi FROM GeneratorInteraction gi
    WHERE gi.generator.author.pseudonym = :#{#id.author()}
    AND gi.generator.name = :#{#id.name()}
    AND gi.user.email = :userEmail
  """)
  Optional<GeneratorInteraction> getByPackageId(PackageId id, String userEmail);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM GeneratorInteraction gi
    WHERE EXISTS (
      SELECT 1 FROM gi.generator g
      WHERE g.author.pseudonym = :#{#id.author()}
      AND g.name = :#{#id.name()}
    )
  """)
  void deleteByPackageId(PackageId id);

  @Query("""
    SELECT COUNT(gi) FROM GeneratorInteraction gi
    WHERE gi.generator.author.pseudonym = :#{#id.author()}
    AND gi.generator.name = :#{#id.name()}
    AND gi.liked = TRUE
  """)
  long likesByPackageId(PackageId id);

  @Query("""
    SELECT COUNT(gi) FROM GeneratorInteraction gi
    WHERE gi.generator.author.pseudonym = :#{#id.author()}
    AND gi.generator.name = :#{#id.name()}
    AND gi.liked = FALSE
  """)
  long dislikesByPackageId(PackageId id);
}
