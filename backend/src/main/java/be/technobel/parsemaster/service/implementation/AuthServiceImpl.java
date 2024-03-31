package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.AuthDTO;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.form.ChangePasswordForm;
import be.technobel.parsemaster.form.LoginForm;
import be.technobel.parsemaster.form.RequestPasswordForm;
import be.technobel.parsemaster.repository.UserRepository;
import be.technobel.parsemaster.security.JWTProvider;
import be.technobel.parsemaster.service.declaration.AuthService;
import be.technobel.parsemaster.service.declaration.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Comparator;
import java.util.TreeSet;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JWTProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;
  private final UidService uidService;
  private final EmailService emailService;

  private record PasswordRequest(String token, String email, Instant expiration) {}
  private final long PASSWORD_REQUEST_EXPIRATION = 1000L*60*5; // milliseconds
  private final String PASSWORD_REQUEST_URL;
  private final TreeSet<PasswordRequest> passwordRequests = new TreeSet<>(
    Comparator.comparing(PasswordRequest::expiration)
  );

  public AuthServiceImpl(
    @Value("${spring.profiles.active}") String activeProfile,
    @Value("${app.http-host}") String httpHost,
    UserRepository userRepository,
    AuthenticationManager authenticationManager,
    JWTProvider jwtProvider,
    PasswordEncoder passwordEncoder,
    UidService uidService,
    EmailService emailService
  ) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.jwtProvider = jwtProvider;
    this.passwordEncoder = passwordEncoder;
    this.uidService = uidService;
    this.emailService = emailService;
    if(activeProfile.equals("prod")) {
      this.PASSWORD_REQUEST_URL = httpHost+"/auth/changePassword/";
    } else {
      this.PASSWORD_REQUEST_URL = "http://localhost:4200/auth/changePassword/";
    }
  }

  @Override
  public AuthDTO signIn(LoginForm form) {
    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(form.login(), form.password())
      );
      final var user = userRepository
        .findByEmailOrPseudonym(form.login())
        .orElseThrow(Exceptions.USER_NOT_FOUND::create);
      return new AuthDTO(
        jwtProvider.generateToken(user.getUsername(), user.getRole())
      );
    }
    catch(Exception ex) {
      throw Exceptions.BAD_CREDENTIALS.create();
    }
  }

  @Override
  public void signOut(HttpServletRequest request) {
    final var token = jwtProvider.extractToken(request);
    if(token != null && jwtProvider.validateToken(token)) jwtProvider.revoke(token);
    else throw Exceptions.BAD_TOKEN.create();
  }

  @Override
  public void requestPassword(RequestPasswordForm form) {
    cleanPasswordTokens();
    final var email = form.email();
    final var notExists = passwordRequests.stream().noneMatch(t -> t.email().equals(email));
    if(notExists) {
      final var token = uidService.generateId("request-password");
      passwordRequests.add(new PasswordRequest(
        token, email, Instant.now().plusMillis(PASSWORD_REQUEST_EXPIRATION)
      ));
      emailService.passwordModificationRequest(email, PASSWORD_REQUEST_URL+token);
    }
  }

  @Override
  public void changePassword(ChangePasswordForm form, String token) {
    cleanPasswordTokens();
    final var request = passwordRequests.stream().filter(t -> t.token.equals(token)).findFirst()
      .orElseThrow(Exceptions.TOKEN_NOT_FOUND::create);
    final var user = userRepository.findByEmail(request.email())
      .orElseThrow(Exceptions.USER_NOT_FOUND::create);
    user.setPasswordHash(passwordEncoder.encode(form.password()));
    userRepository.save(user);
    passwordRequests.remove(request);
    emailService.passwordModificationConfirmation(request.email());
  }

  @Override
  public boolean existsPasswordToken(String token) {
    cleanPasswordTokens();
    return passwordRequests.stream().anyMatch(t -> t.token.equals(token));
  }

  private void cleanPasswordTokens() {
    final var now = Instant.now();
    passwordRequests.removeIf(revoked -> revoked.expiration().isBefore(now));
  }
}
