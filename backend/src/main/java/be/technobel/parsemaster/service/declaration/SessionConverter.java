package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.SessionDTO;
import be.technobel.parsemaster.entity.Session;

public interface SessionConverter {
  SessionDTO toDTO(Session session);
}
