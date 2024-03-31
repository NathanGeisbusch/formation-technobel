package be.technobel.parsemaster;

import be.technobel.parsemaster.dto.CreatedDTO;
import be.technobel.parsemaster.entity.ParserVersion;
import be.technobel.parsemaster.entity.User;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.ParserInfoForm;
import be.technobel.parsemaster.repository.PackageFileRepository;
import be.technobel.parsemaster.repository.ParserRepository;
import be.technobel.parsemaster.repository.ParserVersionRepository;
import be.technobel.parsemaster.repository.UserRepository;
import be.technobel.parsemaster.service.declaration.ParserConverter;
import be.technobel.parsemaster.util.PackageId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("testing")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ParserControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private ParserRepository parserRepository;
  @Autowired private ParserVersionRepository versionRepository;
  @Autowired private PackageFileRepository fileRepository;
  @Autowired private ParserConverter parserConverter;
  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  private static final String USER_EMAIL = "user@test.be";
  private User user;

  @BeforeEach
  public void setup() {
    user = new User("user", USER_EMAIL, "password");
    userRepository.save(user);
    ParserVersion version = parserConverter.create(user, "existing");
    parserRepository.save(version.getInfo());
    fileRepository.saveAll(List.of(
      version.getParserFile(),
      version.getBuilderFile(),
      version.getDocFile()
    ));
    versionRepository.save(version);
  }

  @AfterEach
  public void cleanup() {
    versionRepository.deleteAll();
    fileRepository.deleteAll();
    parserRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void createParser_not_auth() throws Exception {
    var requestPath = "/api/v0.0.1/parsers";
    var form = new PackageCreateForm("test", null, null);
    var formJSON = objectMapper.writeValueAsString(form);
    final var exception = new HashMap<String, Object>();
    exception.put("status", 401);
    exception.put("path", requestPath);
    exception.put("error", Exceptions.UNAUTHORIZED.getMessage());
    var exceptionJSON = objectMapper.writeValueAsString(exception);
    mockMvc.perform(post(requestPath)
        .content(formJSON)
        .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isUnauthorized())
      .andDo(print())
      .andExpect(content().json(exceptionJSON));
  }

  @Test
  @WithMockUser(username = USER_EMAIL, roles = "USER")
  public void createParser() throws Exception {
    var id = new PackageId(user.getPseudonym(), "test", 0);
    var form = new PackageCreateForm(id.name(), null, null);
    var idJSON = objectMapper.writeValueAsString(new CreatedDTO(id.toString()));
    var formJSON = objectMapper.writeValueAsString(form);
    mockMvc.perform(post("/api/v0.0.1/parsers")
        .content(formJSON)
        .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(content().json(idJSON));
  }

  @Test
  @WithMockUser(username = USER_EMAIL, roles = "USER")
  public void getPublicParser() throws Exception {
    var id = new PackageId(user.getPseudonym(), "existing", 0);
    mockMvc.perform(get("/api/v0.0.1/parsers/{id}/public", id))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = USER_EMAIL, roles = "USER")
  public void updateParser() throws Exception {
    var id = new PackageId(user.getPseudonym(), "existing", 0);
    var newName = id.name()+"2";
    var form = new ParserInfoForm(
      newName, "description", null, null, null
    );
    var formJSON = objectMapper.writeValueAsString(form);
    mockMvc.perform(patch("/api/v0.0.1/parsers/{id}", id)
        .content(formJSON)
        .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk());
    var parser = parserRepository.getByNameAndEmail(newName, USER_EMAIL);
    assertTrue(parser.isPresent());
  }

  @Test
  @WithMockUser(username = USER_EMAIL, roles = "USER")
  public void deleteParser() throws Exception {
    var id = new PackageId(user.getPseudonym(), "existing", 0);
    mockMvc.perform(delete("/api/v0.0.1/parsers/{id}", id))
      .andExpect(status().isOk());
  }
}
