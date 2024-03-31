package be.technobel.parsemaster.openapi;

import be.technobel.parsemaster.dto.ErrorForbiddenDTO;
import be.technobel.parsemaster.dto.ErrorFormDTO;
import be.technobel.parsemaster.dto.ErrorNotFoundDTO;
import be.technobel.parsemaster.dto.ErrorUnauthorizedDTO;
import be.technobel.parsemaster.form.PackagesDeleteForm;
import be.technobel.parsemaster.form.SessionsDeleteForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RequestMapping(path = "/api/v0.0.1/delete", produces = "application/json")
public interface BulkDeleteApi {
  @Operation(
    summary = "Delete parsers",
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
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/parsers")
  void deleteParsers(
    @RequestBody PackagesDeleteForm form,
    @RequestParam(required = false) Boolean allVersions,
    Authentication auth
  );

  @Operation(
    summary = "Delete generators",
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
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/generators")
  void deleteGenerators(
    @RequestBody PackagesDeleteForm form,
    @RequestParam(required = false) Boolean allVersions,
    Authentication auth
  );

  @Operation(
    summary = "Delete sessions",
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
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/sessions")
  void deleteSessions(
    @RequestBody SessionsDeleteForm form,
    Authentication auth
  );
}
