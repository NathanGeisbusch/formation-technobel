package be.technobel.parsemaster.entity;

import be.technobel.parsemaster.enumeration.UserRole;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "account")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "pseudonym", nullable = false)
  private String pseudonym;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "role", nullable = false)
  @Enumerated(value = EnumType.ORDINAL)
  private UserRole role;

  @Column(name = "is_activated", nullable = false)
  private Boolean isActivated;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public User() {}

  public User(String pseudonym, String email, String passwordHash) {
    this.pseudonym = pseudonym;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = UserRole.USER;
    this.isActivated = true;
    this.updatedAt = this.createdAt = LocalDateTime.now();
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getPseudonym() {
    return pseudonym;
  }

  public void setPseudonym(String pseudonym) {
    this.pseudonym = pseudonym;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public Boolean getActivated() {
    return isActivated;
  }

  public void setActivated(Boolean activated) {
    isActivated = activated;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.authorities;
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActivated && deletedAt == null;
  }
}
