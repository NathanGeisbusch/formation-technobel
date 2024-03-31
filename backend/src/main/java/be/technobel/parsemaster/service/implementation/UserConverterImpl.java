package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.AccountDTO;
import be.technobel.parsemaster.entity.User;
import be.technobel.parsemaster.form.AccountForm;
import be.technobel.parsemaster.form.RegisterForm;
import be.technobel.parsemaster.service.declaration.UserConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserConverterImpl implements UserConverter {
  private final PasswordEncoder passwordEncoder;

  public UserConverterImpl(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public AccountDTO toAccountDTO(User user) {
    return new AccountDTO(user.getPseudonym(), user.getEmail());
  }

  @Override
  public void fromAccountForm(User user, AccountForm form) {
    final var now = LocalDateTime.now();
    if(form.email() != null) user.setEmail(form.email());
    if(form.pseudonym() != null) user.setPseudonym(form.pseudonym());
    if(form.password() != null) user.setPasswordHash(
      passwordEncoder.encode(form.password())
    );
    user.setUpdatedAt(now);
  }

  @Override
  public User fromRegisterForm(RegisterForm form) {
    return new User(
      form.pseudonym(),
      form.email(),
      passwordEncoder.encode(form.password())
    );
  }
}
