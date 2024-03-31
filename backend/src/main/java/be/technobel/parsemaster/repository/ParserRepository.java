package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.Parser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface ParserRepository extends JpaRepository<Parser, Long> {
  @Query("""
    SELECT COUNT(p) > 0 FROM Parser p
    WHERE p.name = :name AND p.author.email = :userEmail
  """)
  boolean existsByNameAndEmail(String name, String userEmail);

  @Query("""
    SELECT p FROM Parser p
    WHERE p.name = :name AND p.author.email = :userEmail
  """)
  Optional<Parser> getByNameAndEmail(String name, String userEmail);

  @Query("""
    SELECT p FROM Parser p
    WHERE p.name = :name AND p.author.pseudonym = :author
  """)
  Optional<Parser> getByNameAndAuthor(String name, String author);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM ParserVersion pv
    WHERE EXISTS (
      SELECT 1 FROM pv.info p JOIN p.author a
      WHERE a.pseudonym = :author AND p.name = :name
    )
  """)
  void deleteVersions(String name, String author);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM Parser p
    WHERE EXISTS (
      SELECT 1 FROM p.author a
      WHERE a.pseudonym = :author AND p.name = :name
    )
  """)
  void delete(String name, String author);
}
