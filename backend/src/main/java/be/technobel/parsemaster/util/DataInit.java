package be.technobel.parsemaster.util;

import be.technobel.parsemaster.entity.*;
import be.technobel.parsemaster.enumeration.CompressionEncoding;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.form.RegisterForm;
import be.technobel.parsemaster.repository.*;
import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.declaration.UserConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInit implements InitializingBean {
  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final ParserRepository parserRepository;
  private final ParserVersionRepository parserVersionRepository;
  private final PackageFileRepository packageFileRepository;
  private final ParserInteractionRepository parserInteractionRepository;
  private final CompressionService compressionService;
  private final PasswordEncoder passwordEncoder;
  private final GeneratorRepository generatorRepository;
  private final GeneratorVersionRepository generatorVersionRepository;
  private final GeneratorInteractionRepository generatorInteractionRepository;
  private final SessionRepository sessionRepository;

  public DataInit(
    UserRepository userRepository,
    UserConverter userConverter,
    ParserRepository parserRepository,
    ParserVersionRepository parserVersionRepository,
    PackageFileRepository packageFileRepository,
    ParserInteractionRepository parserInteractionRepository,
    CompressionService compressionService,
    PasswordEncoder passwordEncoder,
    GeneratorRepository generatorRepository,
    GeneratorVersionRepository generatorVersionRepository,
    GeneratorInteractionRepository generatorInteractionRepository,
    SessionRepository sessionRepository
  ) {
    this.userRepository = userRepository;
    this.userConverter = userConverter;
    this.parserRepository = parserRepository;
    this.parserVersionRepository = parserVersionRepository;
    this.packageFileRepository = packageFileRepository;
    this.parserInteractionRepository = parserInteractionRepository;
    this.compressionService = compressionService;
    this.passwordEncoder = passwordEncoder;
    this.generatorRepository = generatorRepository;
    this.generatorVersionRepository = generatorVersionRepository;
    this.generatorInteractionRepository = generatorInteractionRepository;
    this.sessionRepository = sessionRepository;
  }

  @Override
  @Transactional
  public void afterPropertiesSet() throws IOException {
    final var dev_wizard = initUser("dev_wizard");
    final var byte_smith = initUser("byte_smith");
    final var tech_genius = initUser("tech_genius");
    final var script_sage = initUser("script_sage");
    final var cyber_space = initUser("cyber_space");
    final var binary_bard = initUser("binary_bard");
    final var code_ninja_x = initUser("code_ninja_x");
    final var pixel_perfect = initUser("pixel_perfect");
    final var algorithm_alchemist = initUser("algorithm_alchemist");
    final var users = List.of(
      dev_wizard, byte_smith, tech_genius, script_sage, cyber_space,
      binary_bard, code_ninja_x, pixel_perfect, algorithm_alchemist
    );
    userRepository.saveAll(users);
    initParser(users, byte_smith, "number-parser", "add", PackageVisibility.PUBLIC,
      "Parser for extracting numbers.");
    initGenerator(users, byte_smith, "sum-calculator", "add", PackageVisibility.PUBLIC,
      "Computes the sum of the numbers.");
    initGenerator(users, byte_smith, "class-generator", "class", PackageVisibility.PUBLIC,
      "Generates java and typescript classes.");
    initParser(users, byte_smith, "calculator-parser", null, PackageVisibility.PUBLIC,
      "Parser for a calculator.\nExtract numbers and operators.");
    initGenerator(users, byte_smith, "calculator", null, PackageVisibility.PUBLIC,
      "Calculator (supports addition, subtraction, multiplication, division).");
    initGenerator(users, pixel_perfect, "optimized-cutting", null, PackageVisibility.PROTECTED,
      "Calculates the distribution of different lengths according to a reference length.");
    initParser(users, byte_smith, "jwt-parser", null, PackageVisibility.PUBLIC,
      "Parser for JWT Token.");
    initGenerator(users, cyber_space, "jwt-decoder", null, PackageVisibility.PUBLIC,
      "JWT Token decoder.");
    initGenerator(users, byte_smith, "text-prefix", null, PackageVisibility.PRIVATE,
      "Append a prefix or/and suffix to a text.");
    initGenerator(users, byte_smith, "vat-calculator", null, PackageVisibility.PRIVATE,
      "VAT calculator for a list of products.");
    initGenerator(users, cyber_space, "password-generator", null, PackageVisibility.PUBLIC,
      "Password generator.");
    initParser(users, algorithm_alchemist, "function-parser", null, PackageVisibility.PUBLIC,
      "Custom syntax for generating functions.");
    initGenerator(users, algorithm_alchemist, "function-generator", null, PackageVisibility.PUBLIC,
      "Generates functions by using a custom syntax.");
    initParser(users, algorithm_alchemist, "class-parser", null, PackageVisibility.PUBLIC,
      "Custom syntax for generating classes.");
    initGenerator(users, algorithm_alchemist, "class-generator-js", null, PackageVisibility.PUBLIC,
      "Generates Javascript classes by using a custom syntax.");
    initGenerator(users, algorithm_alchemist, "class-generator-kotlin", null, PackageVisibility.PUBLIC,
      "Generates Kotlin classes by using a custom syntax.");
    initGenerator(users, algorithm_alchemist, "class-generator-java", null, PackageVisibility.PUBLIC,
      "Generates Java classes by using a custom syntax.");
    initGenerator(users, algorithm_alchemist, "class-generator-c++", null, PackageVisibility.PUBLIC,
      "Generates C++ classes by using a custom syntax.");
    initGenerator(users, code_ninja_x, "kotlin-to-java-constructor", null, PackageVisibility.PUBLIC,
      "Extract parameters from Kotlin class constructor and generates Java class.");
    initGenerator(users, code_ninja_x, "kotlin-to-java-variables", null, PackageVisibility.PUBLIC,
      "Extract Kotlin variables and converts them to Java.");
    initParser(users, binary_bard, "csv-parser", null, PackageVisibility.PUBLIC,
      "Parser for CSV format.");
    initParser(users, binary_bard, "xml-parser", null, PackageVisibility.PUBLIC,
      "Parser for XML format");
    initParser(users, binary_bard, "json-parser", null, PackageVisibility.PUBLIC,
      "Parser for JSON format");
    initParser(users, binary_bard, "yaml-parser", null, PackageVisibility.PUBLIC,
      "Parser for YAML format");
    initGenerator(users, dev_wizard, "openapi-to-spring", null, PackageVisibility.PUBLIC,
      "Convert an openapi json to a spring project");
    initGenerator(users, dev_wizard, "csv-calculator-template", null, PackageVisibility.PUBLIC,
      "Template for computing values of a CSV file.");
    initParser(users, script_sage, "openapi-custom-syntax", null, PackageVisibility.PUBLIC,
      "Custom syntax for writing openapi documentation.");
    initParser(users, tech_genius, "kotlin-class-parser", null, PackageVisibility.PUBLIC,
      "Kotlin class parser.");
    addVersion();
    addSession();
  }

  private User initUser(String name) {
    return userConverter.fromRegisterForm(
      new RegisterForm(name+"@parsemaster.be", name, "123")
    );
  }

  private void initParser(
    List<User> users, User user, String name, String folder,
    PackageVisibility visibility, String description
  ) throws IOException {
    final var version = folder == null ?
      new ParserVersion(
        new Parser(name, user),
        compressionService.createFile(),
        compressionService.createFile(),
        compressionService.createFile()
      ) :
      new ParserVersion(
        new Parser(name, user),
        loadCodeExample(folder+"/parser.txt"),
        loadCodeExample(folder+"/builder.txt"),
        loadCodeExample(folder+"/doc.txt")
      );
    if(visibility == PackageVisibility.PROTECTED) {
      version.setPassword(passwordEncoder.encode("test"));
    }
    version.setVersion(randomVersion());
    version.setUpdatedAt(randomDate());
    version.setDescription(description);
    final var entity = saveParser(visibility, version);
    initParserInteractions(users, user, entity);
  }

  private void initGenerator(
    List<User> users, User user, String name, String folder,
    PackageVisibility visibility, String description
  ) throws IOException {
    final var version = folder == null ?
      new GeneratorVersion(
        new Generator(name, user),
        compressionService.createFile(),
        compressionService.createFile(),
        compressionService.createFile(),
        compressionService.createFile()
      ) : new GeneratorVersion(
        new Generator(name, user),
        loadCodeExample(folder+"/parser.txt"),
        loadCodeExample(folder+"/builder.txt"),
        loadCodeExample(folder+"/generator.txt"),
        loadCodeExample(folder+"/doc.txt")
      );
    if(visibility == PackageVisibility.PROTECTED) {
      version.setPassword(passwordEncoder.encode("test"));
    }
    version.setVersion(randomVersion());
    version.setUpdatedAt(randomDate());
    version.setDescription(description);
    final var entity = saveGenerator(visibility, version);
    initGeneratorInteractions(users, user, entity);
  }

  private Parser saveParser(PackageVisibility visibility, ParserVersion version) {
    version.setVisibility(visibility);
    final var entity = parserRepository.save(version.getInfo());
    packageFileRepository.saveAll(List.of(
      version.getParserFile(),
      version.getBuilderFile(),
      version.getDocFile()
    ));
    parserVersionRepository.save(version);
    return entity;
  }

  private Generator saveGenerator(PackageVisibility visibility, GeneratorVersion version) {
    version.setVisibility(visibility);
    final var entity = generatorRepository.save(version.getInfo());
    packageFileRepository.saveAll(List.of(
      version.getParserFile(),
      version.getBuilderFile(),
      version.getGeneratorFile(),
      version.getDocFile()
    ));
    generatorVersionRepository.save(version);
    return entity;
  }

  private void initParserInteractions(List<User> users, User user, Parser parser) {
    try {
      final var rand = SecureRandom.getInstanceStrong();
      parserInteractionRepository.saveAll(
        users.stream().filter(u -> u != user).map(u -> {
          final var liked = rand.nextBoolean() ? Boolean.TRUE : rand.nextInt(5) == 0 ? false : null;
          final var bookmarked = rand.nextInt(5) == 0;
          return new ParserInteraction(u, parser, liked, bookmarked);
        }).toList()
      );
    } catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private void initGeneratorInteractions(List<User> users, User user, Generator generator) {
    try {
      final var rand = SecureRandom.getInstanceStrong();
      generatorInteractionRepository.saveAll(
        users.stream().filter(u -> u != user).map(u -> {
          final var liked = rand.nextBoolean() ? Boolean.TRUE : rand.nextInt(5) == 0 ? false : null;
          final var bookmarked = rand.nextInt(5) == 0;
          return new GeneratorInteraction(u, generator, liked, bookmarked);
        }).toList()
      );
    } catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private Long randomVersion() {
    try {
      final var rand = SecureRandom.getInstanceStrong();
      final var major = rand.nextLong(6);
      final var minor = rand.nextLong(16);
      final var patch = rand.nextLong(16);
      return (major<<16) | (minor<<8) | patch;
    } catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private LocalDateTime randomDate() {
    try {
      final var rand = SecureRandom.getInstanceStrong();
      var date = LocalDateTime.now();
      if(rand.nextBoolean()) date = date.minusYears(rand.nextLong(2));
      if(rand.nextBoolean()) date = date.minusMonths(rand.nextLong(12));
      if(rand.nextBoolean()) date = date.minusDays(rand.nextLong(30));
      if(rand.nextBoolean()) date = date.minusHours(rand.nextLong(24));
      if(rand.nextBoolean()) date = date.minusMinutes(rand.nextLong(60));
      if(rand.nextBoolean()) date = date.minusSeconds(rand.nextLong(60));
      return date;
    } catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private PackageFile loadCodeExample(String filename) throws IOException {
    try(
      final var input = new ClassPathResource("code-examples/"+filename).getInputStream()
    ) {
      final var content = compressionService.encode(input);
      final var file = new PackageFile(content);
      file.setEncoding(CompressionEncoding.GZIP);
      return file;
    }
  }

  private void addVersion() throws IOException {
    final var byte_smith = userRepository.findByEmail("byte_smith@parsemaster.be").orElseThrow();
    final var lastVersion = parserVersionRepository.findVersions(
      Pageable.ofSize(1), byte_smith.getEmail(),
      byte_smith.getPseudonym(), "number-parser", 0, Long.MAX_VALUE
    ).getContent().get(0);
    final var pv = parserVersionRepository.getByPackageId(new PackageId(
      byte_smith.getPseudonym(), "number-parser", lastVersion
    )).orElseThrow();
    final var info = pv.getInfo();
    final var version = new ParserVersion(
      info,
      loadCodeExample("add/parser.txt"),
      loadCodeExample("add/builder.txt"),
      loadCodeExample("add/doc.txt")
    );
    version.setVersion(lastVersion+1);
    version.setUpdatedAt(pv.getUpdatedAt());
    version.setDescription(pv.getDescription());
    saveParser(PackageVisibility.PRIVATE, version);
  }

  private void addSession() {
    final var byte_smith = userRepository.findByEmail("byte_smith@parsemaster.be").orElseThrow();
    final var lastVersion = generatorVersionRepository.findVersions(
      Pageable.ofSize(1), byte_smith.getEmail(),
      byte_smith.getPseudonym(), "sum-calculator", 0, Long.MAX_VALUE
    ).getContent().get(0);
    final var pv = generatorVersionRepository.getByPackageId(new PackageId(
      byte_smith.getPseudonym(), "sum-calculator", lastVersion
    )).orElseThrow();
    final var session = new Session(
      pv, "mes-calculs-2024", byte_smith,
      compressionService.createFile()
    );
    packageFileRepository.save(session.getInputFile());
    sessionRepository.save(session);
  }
}
