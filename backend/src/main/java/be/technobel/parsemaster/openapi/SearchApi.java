package be.technobel.parsemaster.openapi;

import be.technobel.parsemaster.dto.ErrorFormDTO;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Tag(name = "search")
@Validated
@RequestMapping(path = "/api/v0.0.1/search", produces = "application/json")
public interface SearchApi {
  @Operation(
    summary = "Check if a pseudonym exists",
    parameters = {
      @Parameter(name = "value", example = Examples.PSEUDONYM)
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @GetMapping(path = "/pseudonym")
  boolean existsPseudonym(
    @RequestParam(defaultValue = "") @Size(max = 64, message = "length>64") String value
  );

  @Operation(
    summary = "Check if an email exists",
    parameters = {
      @Parameter(name = "value", example = Examples.EMAIL)
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @GetMapping(path = "/email")
  boolean existsEmail(
    @RequestParam(defaultValue = "") @Size(max = 255, message = "length>255") String value
  );

  @Operation(
    summary = "Check if a parser name exists",
    parameters = {
      @Parameter(name = "name", example = Examples.PARSER_NAME),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/parser/name")
  boolean existsParserByName(
    @RequestParam(defaultValue = "") @Size(max = 64, message = "length>64") String name,
    Authentication auth
  );

  @Operation(
    summary = "Check if a parser version exists",
    parameters = {
      @Parameter(name = "name", example = Examples.PARSER_NAME),
      @Parameter(name = "version", example = Examples.VERSION),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/parser/version")
  boolean existsParserByVersion(
    @RequestParam(defaultValue = "") @Size(max = 64, message = "length>64") String name,
    @RequestParam(defaultValue = "") @Size(max = 13, message = "length>13") String version,
    Authentication auth
  );

  @Operation(
    summary = "Check if a generator name exists",
    parameters = {
      @Parameter(name = "name", example = Examples.GENERATOR_NAME),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/generator/name")
  boolean existsGeneratorByName(
    @RequestParam(defaultValue = "") @Size(max = 64, message = "length>64") String name,
    Authentication auth
  );

  @Operation(
    summary = "Check if a parser version exists",
    parameters = {
      @Parameter(name = "name", example = Examples.GENERATOR_NAME),
      @Parameter(name = "version", example = Examples.VERSION),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/generator/version")
  boolean existsGeneratorByVersion(
    @RequestParam(defaultValue = "") @Size(max = 64, message = "length>64") String name,
    @RequestParam(defaultValue = "") @Size(max = 13, message = "length>13") String version,
    Authentication auth
  );

  @Operation(
    summary = "Check if a session name exists",
    parameters = {
      @Parameter(name = "name", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/session/name")
  boolean existsSessionByName(
    @RequestParam(defaultValue = "") @Size(max = 64, message = "length>64") String name,
    Authentication auth
  );

  @Operation(
    summary = "List the 10 last versions of a parser starting by the parameter value",
    parameters = {
      @Parameter(name = "author", example = Examples.PSEUDONYM),
      @Parameter(name = "name", example = Examples.PARSER_NAME),
      @Parameter(name = "value", example = "0.0."),
    },
    responses = {
      @ApiResponse(responseCode = "200", content = {
        @Content(examples = {@ExampleObject(value = "[\"0.0.3\", \"0.0.2\", \"0.0.1\"]")})
      }),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
    }
  )
  @GetMapping(path = "/parser/versions")
  List<String> findParserVersions(
    @RequestParam @Pattern(regexp = Regex.NAME, message = "name") String author,
    @RequestParam @Pattern(regexp = Regex.NAME, message = "name") String name,
    @RequestParam @Size(max = 13, message = "length>13") String value,
    Authentication auth
  );

  @Operation(
    summary = "List the 10 last versions of a generator starting by the parameter value",
    parameters = {
      @Parameter(name = "author", example = Examples.PSEUDONYM),
      @Parameter(name = "name", example = Examples.GENERATOR_NAME),
      @Parameter(name = "value", example = "0.0."),
    },
    responses = {
      @ApiResponse(responseCode = "200", content = {
        @Content(examples = {@ExampleObject(value = "[\"0.0.3\", \"0.0.2\", \"0.0.1\"]")})
      }),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
    }
  )
  @GetMapping(path = "/generator/versions")
  List<String> findGeneratorVersions(
    @RequestParam @Pattern(regexp = Regex.NAME, message = "name") String author,
    @RequestParam @Pattern(regexp = Regex.NAME, message = "name") String name,
    @RequestParam @Size(max = 13, message = "length>13") String value,
    Authentication auth
  );
}
