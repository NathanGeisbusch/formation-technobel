package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.AuthDTO;
import be.technobel.parsemaster.form.ChangePasswordForm;
import be.technobel.parsemaster.form.LoginForm;
import be.technobel.parsemaster.form.RequestPasswordForm;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
  AuthDTO signIn(LoginForm form);

  void signOut(HttpServletRequest request);

  void requestPassword(RequestPasswordForm form);

  void changePassword(ChangePasswordForm form, String token);

  boolean existsPasswordToken(String token);
}
