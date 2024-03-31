package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.dto.PageDTO;
import be.technobel.parsemaster.dto.SessionDTO;
import be.technobel.parsemaster.enumeration.SortSession;
import be.technobel.parsemaster.form.SessionsDeleteForm;
import be.technobel.parsemaster.form.SessionCreateForm;
import be.technobel.parsemaster.form.SessionEditForm;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionService {
  PageDTO<SessionDTO> find(Integer page, Integer size, String search, SortSession sort, String userEmail);

  SessionDTO get(String id, String userEmail);

  CreatedDTO create(SessionCreateForm form, String userEmail);

  void update(String id, SessionEditForm form, String userEmail);

  void delete(String id, String userEmail);

  void delete(SessionsDeleteForm form, String userEmail);

  byte[] getInputText(String id, String userEmail);

  byte[] getParserCode(String id, String userEmail);

  byte[] getBuilderCode(String id, String userEmail);

  byte[] getGeneratorCode(String id, String userEmail);

  byte[] getDocCode(String id, String userEmail);

  void updateInputText(String id, HttpServletRequest request, String userEmail);
}
