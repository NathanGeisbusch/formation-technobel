package be.technobel.parsemaster.openapi;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.form.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth")
@Validated
@RequestMapping(path = "/api/v0.0.1/auth", produces = "application/json")
public interface AuthApi {
  @Operation(
    summary = "Register a new user",
    responses = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "409", content = {
        @Content(schema = @Schema(implementation = ErrorConflictDTO.class))
      })
    }
  )
  @PostMapping("/sign-up")
  CreatedDTO signUp(@RequestBody RegisterForm form);

  @Operation(
    summary = "Authenticate and get authentication token",
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      })
    }
  )
  @PostMapping("/sign-in")
  AuthDTO signIn(@RequestBody LoginForm form);

  @Operation(
    summary = "Disconnect and revoke token",
    security = {@SecurityRequirement(name = "USER")},
    responses = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/sign-out")
  void signOut(HttpServletRequest request);

  @Operation(
    summary = "Request a password change (send link by mail)",
    responses = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PostMapping("/password")
  void requestPassword(@RequestBody RequestPasswordForm form);

  @Operation(
    summary = "Change password of the user identified by token",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "token", example = Examples.TOKEN)
    },
    responses = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "404", content = {
        @Content(schema = @Schema(implementation = ErrorNotFoundDTO.class))
      })
    }
  )
  @PostMapping("/password/{token}")
  void changePassword(@RequestBody ChangePasswordForm form, @PathVariable String token);

  @Operation(
    summary = "Check if the password change token exists",
    parameters = {
      @Parameter(in = ParameterIn.PATH, name = "token", example = Examples.TOKEN)
    },
    responses = {
      @ApiResponse(responseCode = "200")
    }
  )
  @GetMapping("/password/{token}")
  boolean existsPasswordToken(@PathVariable String token);

  @Operation(
    summary = "Get authenticated user account information",
    security = {@SecurityRequirement(name = "USER")},
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/account")
  AccountDTO account(Authentication auth);

  @Operation(
    summary = "Update authenticated user account information",
    security = {@SecurityRequirement(name = "USER")},
    responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", content = {
        @Content(schema = @Schema(implementation = ErrorFormDTO.class))
      }),
      @ApiResponse(responseCode = "401", content = {
        @Content(schema = @Schema(implementation = ErrorUnauthorizedDTO.class))
      }),
      @ApiResponse(responseCode = "403", content = {
        @Content(schema = @Schema(implementation = ErrorForbiddenDTO.class))
      })
    }
  )
  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/account")
  void updateAccount(@RequestBody AccountForm form, Authentication auth);
}
