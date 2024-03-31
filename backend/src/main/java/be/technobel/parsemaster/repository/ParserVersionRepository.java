package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.ParserVersion;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ParserVersionRepository extends JpaRepository<ParserVersion, Long> {
  @Query("""
    SELECT pv.version FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.pseudonym = :author AND p.name = :name
      AND pv.version >= :minVersion AND pv.version <= :maxVersion
      AND (
        pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
        OR u.email = :userEmail
      )
  """)
  Page<Long> findVersions(
    Pageable pageable, String userEmail, String author, String name,
    long minVersion, long maxVersion
  );

  @Query("""
    SELECT COUNT(pv) > 0 FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.pseudonym = :#{#id.author()}
      AND p.name = :#{#id.name()}
      AND pv.version = :#{#id.version()}
      AND (
        pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
        OR (:userEmail IS NOT NULL AND u.email = :userEmail)
        OR (
          pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PROTECTED
          AND :password IS NOT NULL AND pv.password = :password
        )
      )
  """)
  boolean canAccess(PackageId id, String userEmail, String password);

  @Query("""
    SELECT pv FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.pseudonym = :#{#id.author()}
      AND p.name = :#{#id.name()}
      AND pv.version = :#{#id.version()}
      AND (
        pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
        OR (:userEmail IS NOT NULL AND u.email = :userEmail)
        OR (
          pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PROTECTED
          AND :password IS NOT NULL AND pv.password = :password
        )
      )
  """)
  Optional<ParserVersion> getIfCanAccess(PackageId id, String userEmail, String password);

  @Query("""
    SELECT COUNT(pv) > 0 FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.email = :userEmail AND p.name = :name AND pv.version = :version
  """)
  boolean existsByNameAndVersion(String name, String userEmail, long version);

  @Query("""
    SELECT COUNT(pv) FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.pseudonym = :author AND p.name = :name
  """)
  long remainingVersions(String name, String author);

  @Query("""
    SELECT MAX(pv.version) FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.pseudonym = :#{#id.author()} AND p.name = :#{#id.name()}
  """)
  Long getMaxVersion(PackageId id);

  @Query("""
    SELECT pv FROM ParserVersion pv
    JOIN pv.info p
    JOIN p.author u
    WHERE u.pseudonym = :#{#id.author()}
      AND p.name = :#{#id.name()}
      AND pv.version = :#{#id.version()}
  """)
  Optional<ParserVersion> getByPackageId(PackageId id);

  @Query("""
    SELECT pv
    FROM ParserVersion pv
    JOIN pv.info p
    , (
      SELECT p.id as id, COUNT(pi.liked) as likes
      FROM Parser p
      LEFT JOIN ParserInteraction pi ON pi.parser = p AND pi.liked = TRUE
      GROUP BY p.id
    ) p_likes
    , (
      SELECT p.id as id, COUNT(pi.liked) as dislikes
      FROM Parser p
      LEFT JOIN ParserInteraction pi ON pi.parser = p AND pi.liked = FALSE
      GROUP BY p.id
    ) p_dislikes
    , (
      SELECT pv.id as id, CASE
        WHEN UPPER(pv.info.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 3
        WHEN UPPER(pv.info.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 2
        WHEN UPPER(pv.description) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        ELSE 0
      END as relevance
      FROM ParserVersion pv
    ) p_search
    , (
      SELECT pv.info.id as id, MAX(pv.version) as max_version
      FROM ParserVersion pv
      WHERE pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
      GROUP BY pv.info.id
    ) p_version
    WHERE p.id = p_version.id AND pv.version = p_version.max_version
    AND pv.id = p_search.id AND p_search.relevance <> 0
    AND p.id = p_likes.id AND p.id = p_dislikes.id
  """)
  Page<ParserVersion> findPublic(Pageable pageable, String search);

  @Query("""
    SELECT pv
    FROM ParserVersion pv
    JOIN pv.info p
    , (
      SELECT p.id as id, COUNT(pi.liked) as likes
      FROM Parser p
      LEFT JOIN ParserInteraction pi ON pi.parser = p AND pi.liked = TRUE
      GROUP BY p.id
    ) p_likes
    , (
      SELECT p.id as id, COUNT(pi.liked) as dislikes
      FROM Parser p
      LEFT JOIN ParserInteraction pi ON pi.parser = p AND pi.liked = FALSE
      GROUP BY p.id
    ) p_dislikes
    , (
      SELECT pi.parser.id as id FROM ParserInteraction pi
      WHERE pi.user.email = :userEmail AND pi.bookmarked = TRUE
    ) p_bookmarked
    , (
      SELECT pv.id as id, CASE
        WHEN UPPER(pv.info.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 3
        WHEN UPPER(pv.info.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 2
        WHEN UPPER(pv.description) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        ELSE 0
      END as relevance
      FROM ParserVersion pv
    ) p_search
    , (
      SELECT pv.info.id as id, MAX(pv.version) as max_version
      FROM ParserVersion pv
      WHERE pv.visibility = be.technobel.parsemaster.enumeration.PackageVisibility.PUBLIC
      GROUP BY pv.info.id
    ) p_version
    WHERE p.id = p_version.id AND pv.version = p_version.max_version AND p.id = p_bookmarked.id
    AND pv.id = p_search.id AND p_search.relevance <> 0
    AND p.id = p_likes.id AND p.id = p_dislikes.id
  """)
  Page<ParserVersion> findBookmarked(Pageable pageable, String search, String userEmail);

  @Query("""
    SELECT pv
    FROM ParserVersion pv
    JOIN pv.info p
    , (
      SELECT pv.id as id, CASE
        WHEN UPPER(pv.info.name) LIKE CONCAT(:search, '%') ESCAPE '!' THEN 3
        WHEN UPPER(pv.info.name) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 2
        WHEN UPPER(pv.description) LIKE CONCAT('%', :search, '%') ESCAPE '!' THEN 1
        ELSE 0
      END as relevance
      FROM ParserVersion pv
    ) p_search
    , (
      SELECT pv.info.id as id, MAX(pv.version) as max_version
      FROM ParserVersion pv
      WHERE pv.info.author.email = :userEmail AND (
        :visibility IS NULL OR pv.visibility = :visibility
      )
      GROUP BY pv.info.id
    ) p_version
    WHERE p.id = p_version.id AND (:allVersions = TRUE OR pv.version = p_version.max_version)
    AND pv.id = p_search.id AND p_search.relevance <> 0 AND (
      :visibility IS NULL OR pv.visibility = :visibility
    )
  """)
  Page<ParserVersion> findPrivate(
    Pageable pageable, String search, String userEmail,
    Boolean allVersions, PackageVisibility visibility
  );
}
