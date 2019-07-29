package org.oscm.identity.controller;

import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.oidc.request.AuthorizationRequestManager;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.oscm.identity.oidc.response.validation.AuthTokenValidator;
import org.oscm.identity.oidc.response.validation.TokenValidationResult;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Optional;

@RestController
public class MainController {

  private static final String TOKEN_VALIDATION_FAILED_MESSAGE = "Token validation failed. Reason: ";
  private static final String TOKEN_VALID_MESSAGE = "Token valid!";

  private TenantService tenantService;
  private AuthTokenValidator tokenValidator;

  @Autowired
  public MainController(TenantService tenantService, AuthTokenValidator tokenValidator) {
    this.tenantService = tenantService;
    this.tokenValidator = tokenValidator;
  }

  @GetMapping
  public String homePage() {
    return "Welcome to the oscm-identity home page";
  }

  @GetMapping("/login")
  public void loginPage(
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "state", required = false) String state,
      HttpServletResponse response) {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String url =
        AuthorizationRequestManager.buildRequest(configuration.getProvider())
            .baseUrl(configuration.getAuthUrl())
            .clientId(configuration.getClientId())
            .redirectUrl(configuration.getIdTokenRedirectUrl())
            .scope("openid")
            .responseType("id_token")
            .responseMode("form_post")
            // TODO: create nonce which should be validated for id_token
            .nonce("test-nonce")
            .state(state)
            .buildUrl();

    try {
      response.sendRedirect(url);
    } catch (IOException exc) {
      throw new IdentityProviderException("Problem with contacting identity provider", exc);
    }
  }

  @PostMapping("/id_token")
  public void idTokenCallback(
      @RequestParam(value = "id_token", required = false) String idToken,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription,
      HttpServletResponse response)
      throws IOException, ValidationException {

    if (error != null) {
      throw new IdentityProviderException(error + ": " + errorDescription);
    }

    TokenValidationResult validationResult =
        tokenValidator.validate(TokenValidationRequest.of().token(idToken).build());

    if (validationResult.isValid()) {
      response.sendRedirect(state + "?id_token=" + idToken);
    } else {
      throw new ValidationException(
          TOKEN_VALIDATION_FAILED_MESSAGE + validationResult.getValidationFailureReason());
    }
  }

  /**
   * Token validation endpoint
   *
   * @param request validation request wrapper
   * @return HTTP Response
   */
  @PostMapping("/verify_token")
  public ResponseEntity verifyToken(@RequestBody TokenValidationRequest request) {
    TokenValidationResult validationResult = tokenValidator.validate(request);

    if (validationResult.isValid()) return ResponseEntity.ok(TOKEN_VALID_MESSAGE);
    else
      return ResponseEntity.status(406)
          .body(TOKEN_VALIDATION_FAILED_MESSAGE + validationResult.getValidationFailureReason());
  }
}
