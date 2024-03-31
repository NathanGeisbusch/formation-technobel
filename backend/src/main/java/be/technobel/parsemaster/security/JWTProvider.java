package be.technobel.parsemaster.security;

import be.technobel.parsemaster.entity.User;
import be.technobel.parsemaster.enumeration.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Comparator;
import java.util.TreeSet;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JWTProvider {
  private final byte[] JWT_SECRET; // 64 bytes
  private final long EXPIRES_AT;   // Lifetime of a token in seconds
  private final UserDetailsService userDetailsService;

  /** Revoked token with its expiration time */
  private record RevokedToken(String token, Instant expiration) {}

  /** Set of revoked tokens */
  private final TreeSet<RevokedToken> revokedTokens = new TreeSet<>(
    Comparator.comparing(RevokedToken::expiration)
  );

  public JWTProvider(
    @Value("${spring.profiles.active}") String activeProfile,
    UserDetailsService userDetailsService
  ) throws NoSuchAlgorithmException {
    this.userDetailsService = userDetailsService;
    if(activeProfile.equals("prod")) {
      this.EXPIRES_AT = 60*60;
      SecureRandom.getInstanceStrong().nextBytes(this.JWT_SECRET = new byte[64]);
    } else {
      this.EXPIRES_AT = 60*60*24;
      this.JWT_SECRET = "L7pRJuXxbttmnWlnH1ivjrfHclzmJrey1JuxIPp5KMRfQgR90I29HkzCDwAqsD2d"
        .getBytes(StandardCharsets.US_ASCII);
    }
  }

  /** Generates a new jwt token from user email and role */
  public String generateToken(String username, UserRole role) {
    return "Bearer " + JWT.create()
      .withSubject(username)
      .withClaim("role", role.toString())
      .withExpiresAt(Instant.now().plusMillis(1000* EXPIRES_AT))
      .sign(Algorithm.HMAC512(JWT_SECRET));
  }

  /** Returns the jwt token from http request (without Bearer prefix) or returns null */
  public String extractToken(HttpServletRequest req) {
    final var header = req.getHeader("Authorization");
    if(header == null || !header.startsWith("Bearer ")) return null;
    return header.replaceFirst("Bearer ", "");
  }

  /**
   * Verifies the validity of the jwt token.
   * @param token bearer jwt token
   */
  public boolean validateToken(String token) {
    try {
      final var jwt = JWT.require(Algorithm.HMAC512(JWT_SECRET))
        .acceptExpiresAt(EXPIRES_AT)
        .withClaimPresence("sub")
        .withClaimPresence("role")
        .build()
        .verify(token);
      final var username = jwt.getSubject();
      final var user = (User)userDetailsService.loadUserByUsername(username);
      if(!user.isEnabled()) return false;
      final var role = jwt.getClaim("role").as(UserRole.class);
      return user.getRole().equals(role);
    }
    catch(JWTVerificationException|UsernameNotFoundException ex) {
      return false;
    }
  }

  /**
   * Decodes the jwt token and authenticate the user.
   * @param token bearer jwt token
   */
  public Authentication createAuthentication(String token) {
    final var jwt = JWT.decode(token);
    final var username = jwt.getSubject();
    final var userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(
      userDetails.getUsername(), null, userDetails.getAuthorities()
    );
  }

  /**
   * Revokes the jwt token.
   * @param token bearer jwt token
   */
  public void revoke(String token) {
    if(isNotRevoked(token)) {
      revokedTokens.add(new RevokedToken(token, JWT.decode(token).getExpiresAtAsInstant()));
    }
  }

  /**
   * Returns true if the jwt token is not revoked.
   * @param token bearer jwt token
   * @return true if the token is not revoked
   */
  public boolean isNotRevoked(String token) {
    cleanRevoked();
    return revokedTokens.stream().noneMatch(revoked -> revoked.token.equals(token));
  }

  /** Removes expired jwt token from revoke list */
  private void cleanRevoked() {
    final var now = Instant.now();
    revokedTokens.removeIf(revoked -> revoked.expiration().isBefore(now));
  }
}
