package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
  @Query("""
    SELECT COUNT(s) > 0 FROM Session s
    WHERE s.name = :name AND s.author.email = :userEmail
  """)
  boolean existsByName(String name, String userEmail);

  @Query("""
    SELECT s FROM Session s
    WHERE s.name = :name AND s.author.email = :userEmail
  """)
  Optional<Session> getByName(String name, String userEmail);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM GeneratorVersion gv
    WHERE gv.id = :id AND gv.deletedAt IS NOT NULL AND NOT EXISTS (
      SELECT 1 FROM Session s WHERE s.generatorVersion = gv
    )
  """)
  void deleteGeneratorVersion(Long id);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM Generator g
    WHERE g.id = :id AND g.deletedAt IS NOT NULL AND NOT EXISTS (
      SELECT 1 FROM GeneratorVersion gv WHERE gv.info = g
    )
  """)
  void deleteGenerator(Long id);

  @Query("""
    SELECT s FROM Session s, (
      SELECT s.id as id, CASE
        WHEN UPPER(s.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        WHEN UPPER(s.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 2
        ELSE 0
      END as relevance
      FROM Session s WHERE s.author.email = :userEmail
    ) s_search
    WHERE s.id = s_search.id AND s_search.relevance <> 0 AND s.author.email = :userEmail
  """)
  Page<Session> find(Pageable pageable, String search, String userEmail);
}
