package be.technobel.parsemaster.repository;

import be.technobel.parsemaster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("""
    SELECT u FROM User u
    WHERE (u.email = :username OR u.pseudonym = :username)
    AND u.isActivated = TRUE AND u.deletedAt IS NULL
  """)
  Optional<User> findByEmailOrPseudonym(String username);

  @Query("""
    SELECT u FROM User u
    WHERE u.email = :email
    AND u.isActivated = TRUE AND u.deletedAt IS NULL
  """)
  Optional<User> findByEmail(String email);

  @Query("""
    SELECT COUNT(u) > 0 FROM User u
    WHERE u.pseudonym = :pseudonym
  """)
  boolean existsByPseudonym(String pseudonym);

  @Query("""
    SELECT COUNT(u) > 0 FROM User u
    WHERE u.email = :email
  """)
  boolean existsByEmail(String email);

  @Query("""
    SELECT COUNT(u) > 0 FROM User u
    WHERE (
      (:email IS NOT NULL AND (u.email = :email)) OR
      (:pseudonym IS NOT NULL AND (u.pseudonym = :pseudonym))
    )
  """)
  boolean existsByEmailOrPseudonym(String email, String pseudonym);

  @Query("""
    SELECT COUNT(u) > 0 FROM User u
    WHERE u.id <> :id AND (
      (:email IS NOT NULL AND (u.email = :email)) OR
      (:pseudonym IS NOT NULL AND (u.pseudonym = :pseudonym))
    )
  """)
  boolean existsByEmailOrPseudonymAndIsNot(String email, String pseudonym, Long id);
}
