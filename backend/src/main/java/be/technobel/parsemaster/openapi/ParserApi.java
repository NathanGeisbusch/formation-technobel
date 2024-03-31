package be.technobel.parsemaster.openapi;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import be.technobel.parsemaster.enumeration.SortPublicPackage;
import be.technobel.parsemaster.form.PackageCreateForm;
import be.technobel.parsemaster.form.ParserInfoForm;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.MimeTypeUtils;

@Tag(name = "parsers")
@Validated
@RequestMapping(path = "/api/v0.0.1/parsers", produces = "application/json")
public interface ParserApi {
  @Operation(
    summary = "Find public parsers",
    parameters = {
      @Parameter(name = "page", example = "0"),
      @Parameter(name = "size", example = "10"),
      @Parameter(name = "search", example = Examples.PARSER_NAME),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @GetMapping(path = "/public")
  PageDTO<PackagePublicDTO> findPublic(
    @RequestParam(defaultValue = "0") @PositiveOrZero(field = "page") Integer page,
    @RequestParam(defaultValue = "10") @In(field = "size", values = {10, 20, 50}) Integer size,
    @RequestParam(required = false, defaultValue = "") @Size(max = 255, message = "length>255") String search,
    @RequestParam(required = false) SortPublicPackage sort,
    Authentication auth
  );

  @Operation(
    summary = "Find bookmarked parsers",
    parameters = {
      @Parameter(name = "page", example = "0"),
      @Parameter(name = "size", example = "10"),
      @Parameter(name = "search", example = Examples.PARSER_NAME),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/bookmarked")
  PageDTO<PackagePublicDTO> findBookmarked(
    @RequestParam(defaultValue = "0") @PositiveOrZero(field = "page") Integer page,
    @RequestParam(defaultValue = "10") @In(field = "size", values = {10, 20, 50}) Integer size,
    @RequestParam(required = false, defaultValue = "") @Size(max = 255, message = "length>255") String search,
    @RequestParam(required = false) SortPublicPackage sort,
    Authentication auth
  );

  @Operation(
    summary = "Find own parsers",
    parameters = {
      @Parameter(name = "page", example = "0"),
      @Parameter(name = "size", example = "10"),
      @Parameter(name = "name", example = Examples.PARSER_NAME),
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/own")
  PageDTO<PackagePrivateDTO> findOwn(
    @RequestParam(defaultValue = "0") @PositiveOrZero(field = "page") Integer page,
    @RequestParam(defaultValue = "10") @In(field = "size", values = {10, 20, 50}) Integer size,
    @RequestParam(required = false, defaultValue = "") @Size(max = 255, message = "length>255") String search,
    @RequestParam(required = false) Boolean allVersions,
    @RequestParam(required = false) PackageVisibility visibility,
    @RequestParam(required = false) SortPrivatePackage sort,
    Authentication auth
  );

  @Operation(
    summary = "Create a new parser",
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
    @RequestBody PackageCreateForm form,
    Authentication auth
  );

  @Operation(
    summary = "Get public parser info",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @GetMapping("/{id}/public")
  PackagePublicDTO getPublic(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get protected parser info with password",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      }),
    }
  )
  @GetMapping("/{id}/protected")
  PackagePrivateDTO getProtected(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam @Size(max = 255, message = "length>255") String password
  );

  @Operation(
    summary = "Get private parser info",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @GetMapping("/{id}/private")
  PackagePrivateDTO getPrivate(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get editable parser info",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @GetMapping("/{id}/edit")
  ParserEditDTO getEditable(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    Authentication auth
  );

  @Operation(
    summary = "Update parser info",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestBody ParserInfoForm form,
    Authentication auth
  );

  @Operation(
    summary = "Delete parser",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam(required = false) Boolean allVersions,
    Authentication auth
  );

  @Operation(
    summary = "Like/dislike/unlike parser",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @PatchMapping("/{id}/like")
  void like(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam(required = false) Boolean value,
    Authentication auth
  );

  @Operation(
    summary = "Bookmark parser",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @PatchMapping("/{id}/bookmark")
  void bookmark(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam Boolean value,
    Authentication auth
  );

  @Operation(
    summary = "Create a new major version for a parser",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "201"),
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
  @PostMapping("/{id}/major")
  CreatedDTO createMajorVersion(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    Authentication auth
  );

  @Operation(
    summary = "Create a new minor version for a parser",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "201"),
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
  @PostMapping("/{id}/minor")
  CreatedDTO createMinorVersion(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    Authentication auth
  );

  @Operation(
    summary = "Create a new patch version for a parser",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "201"),
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
  @PostMapping("/{id}/patch")
  CreatedDTO createPatchVersion(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    Authentication auth
  );

  @Operation(
    summary = "Get parser code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @GetMapping(path = "/{id}/parser", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getParserCode(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam(required = false) @Size(max = 255, message = "length>255") String password,
    Authentication auth
  );

  @Operation(
    summary = "Get builder code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @GetMapping(path = "/{id}/builder", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getBuilderCode(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam(required = false) @Size(max = 255, message = "length>255") String password,
    Authentication auth
  );

  @Operation(
    summary = "Get documentation code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
    },
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @GetMapping(path = "/{id}/doc", produces = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE)
  byte[] getDocCode(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    @RequestParam(required = false) @Size(max = 255, message = "length>255") String password,
    Authentication auth
  );

  @Operation(
    summary = "Update parser code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @PatchMapping("/{id}/parser")
  void updateParserCode(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    HttpServletRequest request,
    Authentication auth
  );

  @Operation(
    summary = "Update builder code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @PatchMapping("/{id}/builder")
  void updateBuilderCode(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    HttpServletRequest request,
    Authentication auth
  );

  @Operation(
    summary = "Update documentation code",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "id", example = Examples.PARSER),
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
  @PatchMapping("/{id}/doc")
  void updateDocCode(
    @PathVariable @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id") String id,
    HttpServletRequest request,
    Authentication auth
  );
}
