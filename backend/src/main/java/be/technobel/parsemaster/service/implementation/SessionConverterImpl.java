package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.dto.SessionDTO;
import be.technobel.parsemaster.entity.Session;
import be.technobel.parsemaster.service.declaration.SessionConverter;
import be.technobel.parsemaster.util.PackageId;
import org.springframework.stereotype.Service;

@Service
public class SessionConverterImpl implements SessionConverter {
  @Override
  public SessionDTO toDTO(Session session) {
    final var generator = session.getGeneratorVersion();
    final var generatorId = new PackageId(
      session.getAuthor().getPseudonym(),
      generator.getInfo().getName(),
      generator.getVersion()
    ).toString();
    return new SessionDTO(
      session.getName(),
      generatorId,
      session.getUpdatedAt(),
      generator.getParserSyntax(),
      generator.getGeneratorSyntax()
    );
  }
}
