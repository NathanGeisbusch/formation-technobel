package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.Generator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface GeneratorRepository extends JpaRepository<Generator, Long> {
  @Query("""
    SELECT COUNT(g) > 0 FROM Generator g
    WHERE g.name = :name AND g.author.email = :userEmail
    AND g.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM GeneratorVersion gv
      WHERE gv.info = g AND gv.deletedAt IS NULL
    )
  """)
  boolean existsByNameAndEmail(String name, String userEmail);

  @Query("""
    SELECT g FROM Generator g
    WHERE g.name = :name AND g.author.email = :userEmail
    AND g.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM GeneratorVersion gv
      WHERE gv.info = g AND gv.deletedAt IS NULL
    )
  """)
  Optional<Generator> getByNameAndEmail(String name, String userEmail);

  @Query("""
    SELECT g FROM Generator g
    WHERE g.name = :name AND g.author.pseudonym = :author
    AND g.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM GeneratorVersion gv
      WHERE gv.info = g AND gv.deletedAt IS NULL
    )
  """)
  Optional<Generator> getByNameAndAuthor(String name, String author);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM GeneratorVersion gv
    WHERE gv.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM gv.info g JOIN g.author a
      WHERE g.deletedAt IS NULL AND a.pseudonym = :author AND g.name = :name AND NOT EXISTS (
        SELECT 1 FROM Session s WHERE s.generatorVersion = gv
      )
    )
  """)
  void deleteVersions(String name, String author);

  @Transactional
  @Modifying
  @Query("""
    UPDATE GeneratorVersion gv SET gv.deletedAt = CURRENT_TIMESTAMP()
    WHERE gv.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM gv.info g JOIN g.author a
      WHERE g.deletedAt IS NULL AND a.pseudonym = :author AND g.name = :name AND EXISTS (
        SELECT 1 FROM Session s WHERE s.generatorVersion = gv
      )
    )
  """)
  void disableVersions(String name, String author);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM Generator g
    WHERE g.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM g.author a
      WHERE a.pseudonym = :author AND g.name = :name AND NOT EXISTS (
        SELECT 1 FROM GeneratorVersion gv WHERE gv.info = g
      )
    )
  """)
  void delete(String name, String author);

  @Transactional
  @Modifying
  @Query("""
    UPDATE Generator g SET g.deletedAt = CURRENT_TIMESTAMP()
    WHERE g.deletedAt IS NULL AND EXISTS (
      SELECT 1 FROM g.author a
      WHERE a.pseudonym = :author AND g.name = :name AND EXISTS (
        SELECT 1 FROM GeneratorVersion gv WHERE gv.info = g
      )
    )
  """)
  void disable(String name, String author);
}
