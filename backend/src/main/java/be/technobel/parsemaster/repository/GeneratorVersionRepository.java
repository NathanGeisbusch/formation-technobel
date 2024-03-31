package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.GeneratorVersion;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface GeneratorVersionRepository extends JpaRepository<GeneratorVersion, Long> {
  @Query("""
    SELECT gv.version FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL
    JOIN User u ON g.author = u
    WHERE gv.deletedAt IS NULL AND u.pseudonym = :author AND g.name = :name
      AND gv.version >= :minVersion AND gv.version <= :maxVersion
      AND (
        gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
        OR u.email = :userEmail
      )
  """)
  Page<Long> findVersions(
    Pageable pageable, String userEmail, String author, String name,
    long minVersion, long maxVersion
  );

  @Query("""
    SELECT COUNT(gv) > 0 FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    JOIN g.author u
    WHERE u.pseudonym = :#{#id.author()}
      AND g.name = :#{#id.name()}
      AND gv.version = :#{#id.version()}
      AND (
        gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
        OR (:userEmail IS NOT NULL AND u.email = :userEmail)
        OR (
          gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PROTECTED
          AND :password IS NOT NULL AND gv.password = :password
        )
      )
  """)
  boolean canAccess(PackageId id, String userEmail, String password);

  @Query("""
    SELECT gv FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    JOIN g.author u
    WHERE u.pseudonym = :#{#id.author()}
      AND g.name = :#{#id.name()}
      AND gv.version = :#{#id.version()}
      AND (
        gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
        OR (:userEmail IS NOT NULL AND u.email = :userEmail)
        OR (
          gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PROTECTED
          AND :password IS NOT NULL AND gv.password = :password
        )
      )
  """)
  Optional<GeneratorVersion> getIfCanAccess(PackageId id, String userEmail, String password);

  @Query("""
    SELECT COUNT(gv) > 0 FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    JOIN g.author u
    WHERE u.email = :userEmail AND g.name = :name AND gv.version = :version
  """)
  boolean existsByNameAndVersion(String name, String userEmail, long version);

  @Query("""
    SELECT COUNT(gv) FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    JOIN g.author u
    WHERE u.pseudonym = :author AND g.name = :name
  """)
  long remainingVersions(String name, String author);

  @Query("""
    SELECT MAX(gv.version) FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    JOIN g.author u
    WHERE u.pseudonym = :#{#id.author()} AND g.name = :#{#id.name()}
  """)
  Long getMaxVersion(PackageId id);

  @Query("""
    SELECT gv FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    JOIN g.author u
    WHERE u.pseudonym = :#{#id.author()}
      AND g.name = :#{#id.name()}
      AND gv.version = :#{#id.version()}
  """)
  Optional<GeneratorVersion> getByPackageId(PackageId id);

  @Transactional
  @Modifying
  @Query("""
    DELETE FROM GeneratorVersion gv
    WHERE gv.deletedAt IS NULL AND gv.version = :#{#id.version()} AND EXISTS (
      SELECT 1 FROM gv.info g JOIN g.author a
      WHERE g.deletedAt IS NULL AND g.name = :#{#id.name()}
      AND a.pseudonym = :#{#id.author()} AND NOT EXISTS (
        SELECT 1 FROM Session s WHERE s.generatorVersion = gv
      )
    )
  """)
  void deleteVersion(PackageId id);

  @Transactional
  @Modifying
  @Query("""
    UPDATE GeneratorVersion gv SET gv.deletedAt = CURRENT_TIMESTAMP()
    WHERE gv.deletedAt IS NULL AND gv.version = :#{#id.version()} AND EXISTS (
      SELECT 1 FROM gv.info g JOIN g.author a
      WHERE g.deletedAt IS NULL AND g.name = :#{#id.name()}
      AND a.pseudonym = :#{#id.author()} AND EXISTS (
        SELECT 1 FROM Session s WHERE s.generatorVersion = gv
      )
    )
  """)
  void disableVersion(PackageId id);

  @Query("""
    SELECT gv
    FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    , (
      SELECT g.id as id, COUNT(gi.liked) as likes
      FROM Generator g
      LEFT JOIN GeneratorInteraction gi ON gi.generator = g AND gi.liked = TRUE
      GROUP BY g.id
    ) g_likes
    , (
      SELECT g.id as id, COUNT(gi.liked) as dislikes
      FROM Generator g
      LEFT JOIN GeneratorInteraction gi ON gi.generator = g AND gi.liked = FALSE
      GROUP BY g.id
    ) g_dislikes
    , (
      SELECT gv.id as id, CASE
        WHEN UPPER(gv.info.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 3
        WHEN UPPER(gv.info.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 2
        WHEN UPPER(gv.description) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        ELSE 0
      END as relevance
      FROM GeneratorVersion gv
    ) g_search
    , (
      SELECT gv.info.id as id, MAX(gv.version) as max_version
      FROM GeneratorVersion gv
      WHERE gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
      AND gv.info.deletedAt IS NULL AND gv.deletedAt IS NULL
      GROUP BY gv.info.id
    ) g_version
    WHERE g.id = g_version.id AND gv.version = g_version.max_version
    AND gv.id = g_search.id AND g_search.relevance <> 0
    AND g.id = g_likes.id AND g.id = g_dislikes.id
  """)
  Page<GeneratorVersion> findPublic(Pageable pageable, String search);

  @Query("""
    SELECT gv
    FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    , (
      SELECT g.id as id, COUNT(gi.liked) as likes
      FROM Generator g
      LEFT JOIN GeneratorInteraction gi ON gi.generator = g AND gi.liked = TRUE
      GROUP BY g.id
    ) g_likes
    , (
      SELECT g.id as id, COUNT(gi.liked) as dislikes
      FROM Generator g
      LEFT JOIN GeneratorInteraction gi ON gi.generator = g AND gi.liked = FALSE
      GROUP BY g.id
    ) g_dislikes
    , (
      SELECT gi.generator.id as id FROM GeneratorInteraction gi
      WHERE gi.user.email = :userEmail AND gi.bookmarked = TRUE
    ) g_bookmarked
    , (
      SELECT gv.id as id, CASE
        WHEN UPPER(gv.info.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 3
        WHEN UPPER(gv.info.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 2
        WHEN UPPER(gv.description) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        ELSE 0
      END as relevance
      FROM GeneratorVersion gv
    ) g_search
    , (
      SELECT gv.info.id as id, MAX(gv.version) as max_version
      FROM GeneratorVersion gv
      WHERE gv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
      AND gv.info.deletedAt IS NULL AND gv.deletedAt IS NULL
      GROUP BY gv.info.id
    ) g_version
    WHERE g.id = g_version.id AND gv.version = g_version.max_version AND g.id = g_bookmarked.id
    AND gv.id = g_search.id AND g_search.relevance <> 0
    AND g.id = g_likes.id AND g.id = g_dislikes.id
  """)
  Page<GeneratorVersion> findBookmarked(Pageable pageable, String search, String userEmail);

  @Query("""
    SELECT gv
    FROM GeneratorVersion gv
    JOIN Generator g ON gv.info = g AND g.deletedAt IS NULL AND gv.deletedAt IS NULL
    , (
      SELECT gv.id as id, CASE
        WHEN UPPER(gv.info.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 3
        WHEN UPPER(gv.info.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 2
        WHEN UPPER(gv.description) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        ELSE 0
      END as relevance
      FROM GeneratorVersion gv
    ) g_search
    , (
      SELECT gv.info.id as id, MAX(gv.version) as max_version
      FROM GeneratorVersion gv
      WHERE gv.info.author.email = :userEmail AND (
        :visibility IS NULL OR gv.visibility = :visibility
      )
      AND gv.info.deletedAt IS NULL AND gv.deletedAt IS NULL
      GROUP BY gv.info.id
    ) g_version
    WHERE g.id = g_version.id AND (:allVersions = TRUE OR gv.version = g_version.max_version)
    AND gv.id = g_search.id AND g_search.relevance <> 0 AND (
      :visibility IS NULL OR gv.visibility = :visibility
    )
  """)
  Page<GeneratorVersion> findPrivate(
    Pageable pageable, String search, String userEmail,
    Boolean allVersions, PackageVisibility visibility
  );
}
