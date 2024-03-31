package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.AccountDTO;
import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.form.AccountForm;
import be.technobel.parsemaster.form.RegisterForm;

public interface UserService {
  AccountDTO account(String username);

  CreatedDTO register(RegisterForm form);

  void update(AccountForm form, String username);
}
