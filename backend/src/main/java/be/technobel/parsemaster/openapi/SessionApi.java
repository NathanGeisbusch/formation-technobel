package be.technobel.parsemaster.openapi;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.enumeration.SortSession;
import be.technobel.parsemaster.form.SessionCreateForm;
import be.technobel.parsemaster.form.SessionEditForm;
import be.technobel.parsemaster.validation.constraint.In;
import be.technobel.parsemaster.validation.constraint.PositiveOrZero;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "sessions")
@Validated
@RequestMapping(path = "/api/v0.0.1/sessions", produces = "application/json")
public interface SessionApi {
  @Operation(
    summary = "Find sessions",
    parameters = {
      @Parameter(name = "page", example = "0"),
      @Parameter(name = "size", example = "10"),
      @Parameter(name = "search", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  PageDTO<SessionDTO> find(
    @RequestParam(defaultValue = "0") @PositiveOrZero(field = "page") Integer page,
    @RequestParam(defaultValue = "10") @In(field = "size", values = {10, 20, 50}) Integer size,
    @RequestParam(required = false, defaultValue = "") @Size(max = 255, message = "length>255") String search,
    @RequestParam(required = false) SortSession sort,
    Authentication auth
  );

  @Operation(
    summary = "Create a new session",
    responses = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "409", content = {
        @Content(schema = @Schema(implementation = ErrorConflictDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  CreatedDTO create(
    @RequestBody SessionCreateForm form,
    Authentication auth
  );

  @Operation(
    summary = "Get session info",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  SessionDTO get(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Update session info",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      }),
      @ApiResponse(responseCode = "409", content = {
        @Content(schema = @Schema(implementation = ErrorConflictDTO.class))
      }),
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/{id}")
  void update(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    @RequestBody SessionEditForm form,
    Authentication auth
  );

  @Operation(
    summary = "Delete session",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      }),
    }
  )
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  void delete(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get input text",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/{id}/input", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getInputText(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get parser code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/{id}/parser", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getParserCode(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get builder code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/{id}/builder", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getBuilderCode(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get generator code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/{id}/generator", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getGeneratorCode(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get documentation code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/{id}/doc", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getDocCode(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    Authentication auth
  );

  @Operation(
    summary = "Update input text",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.SESSION),
    },
    responses = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFieldDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      }),
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/{id}/input")
  void updateInputText(
    @PathVariable @Pattern(regexp = Regex.NAME, message = "name") String id,
    HttpServletRequest request,
    Authentication auth
  );
}
