package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.AccountDTO;
import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.form.*;
import be.technobel.parsemaster.repository.UserRepository;
import be.technobel.parsemaster.service.declaration.EmailService;
import be.technobel.parsemaster.service.declaration.UserConverter;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements be.technobel.parsemaster.service.declaration.UserService {
  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final EmailService emailService;

  public UserServiceImpl(
    UserRepository userRepository,
    UserConverter userConverter,
    EmailService emailService
  ) {
    this.userRepository = userRepository;
    this.userConverter = userConverter;
    this.emailService = emailService;
  }

  @Override
  public AccountDTO account(String username) {
    final var user = userRepository
      .findByEmailOrPseudonym(username)
      .orElseThrow(Exceptions.USER_NOT_FOUND::create);
    return userConverter.toAccountDTO(user);
  }

  @Override
  public CreatedDTO register(RegisterForm form) {
    if(userRepository.existsByEmailOrPseudonym(form.email(), form.pseudonym())) {
      throw Exceptions.USER_ALREADY_EXISTS.create();
    }
    final var user = userConverter.fromRegisterForm(form);
    userRepository.save(user);
    emailService.userCreation(user.getEmail());
    return new CreatedDTO(user.getPseudonym());
  }

  @Override
  public void update(AccountForm form, String username) {
    final var user = userRepository
      .findByEmailOrPseudonym(username)
      .orElseThrow(Exceptions.USER_NOT_FOUND::create);
    if(userRepository.existsByEmailOrPseudonymAndIsNot(form.email(), form.pseudonym(), user.getId())) {
      throw Exceptions.USER_ALREADY_EXISTS.create();
    }
    userConverter.fromAccountForm(user, form);
    userRepository.save(user);
  }
}
