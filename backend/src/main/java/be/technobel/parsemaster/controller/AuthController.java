package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.dto.AccountDTO;
import be.technobel.parsemaster.dto.AuthDTO;
import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.form.*;
import be.technobel.parsemaster.openapi.AuthApi;
import be.technobel.parsemaster.service.declaration.AuthService;
import be.technobel.parsemaster.service.declaration.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController implements AuthApi {
  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  @Override
  public CreatedDTO signUp(RegisterForm form) {
    return userService.register(form);
  }

  @Override
  public AuthDTO signIn(LoginForm form) {
    return authService.signIn(form);
  }

  @Override
  public void signOut(HttpServletRequest request) {
    authService.signOut(request);
  }

  @Override
  public void requestPassword(RequestPasswordForm form) {
    authService.requestPassword(form);
  }

  @Override
  public void changePassword(ChangePasswordForm form, String token) {
    authService.changePassword(form, token);
  }

  @Override
  public boolean existsPasswordToken(String token) {
    return authService.existsPasswordToken(token);
  }

  @Override
  public AccountDTO account(Authentication auth) {
    return userService.account(auth.getName());
  }

  @Override
  public void updateAccount(AccountForm form, Authentication auth) {
    userService.update(form, auth.getName());
  }
}
