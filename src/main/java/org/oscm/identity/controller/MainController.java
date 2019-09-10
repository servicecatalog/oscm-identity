/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Jun 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.request.*;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.AuthTokenValidator;
import org.oscm.identity.oidc.validation.TokenValidationResult;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
public class MainController {

  private static final String TOKEN_VALIDATION_FAILED_MESSAGE = "Token validation failed. Reason: ";
  private static final String TOKEN_VALID_MESSAGE = "Token valid!";

  private TenantService tenantService;
  private AuthTokenValidator tokenValidator;
  private RequestHandler requestHandler;

  @Autowired
  public MainController(
      TenantService tenantService,
      AuthTokenValidator tokenValidator,
      RequestHandler requestHandler) {
    this.tenantService = tenantService;
    this.tokenValidator = tokenValidator;
    this.requestHandler = requestHandler;
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

    AuthorizationRequest request =
        requestHandler.getRequestManager(configuration.getProvider()).initAuthorizationRequest();
    request.setBaseUrl(configuration.getAuthUrl());
    request.setClientId(configuration.getClientId());
    request.setRedirectUrl(configuration.getRedirectUrl());
    request.setScope(configuration.getAuthUrlScope());
    request.setResponseType("id_token code");
    request.setResponseMode("form_post");
    // TODO: create nonce which should be validated for id_token
    request.setNonce("test-nonce");
    request.setState(state);
    request.execute(response);
  }

  @PostMapping("/callback")
  public void callback(
      @RequestParam(value = "id_token", required = false) String idToken,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription,
      HttpServletResponse response)
      throws IOException, ValidationException, JSONException {

    if (error != null) {
      throw new IdentityProviderException(error + ": " + errorDescription);
    }

    log.info("Authorization code retrieved:" + code);
    // TODO: get tenant out of state param
    String tenantId = null;
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));

    TokenRequest tokenRequest =
        requestHandler.getRequestManager(configuration.getProvider()).initTokenRequest();
    tokenRequest.setBaseUrl(configuration.getTokenUrl());
    tokenRequest.setClientId(configuration.getClientId());
    tokenRequest.setClientSecret(configuration.getClientSecret());
    tokenRequest.setCode(code);
    tokenRequest.setGrantType("authorization_code");
    tokenRequest.setRedirectUrl(configuration.getRedirectUrl());

    ResponseEntity entity = tokenRequest.execute();

    JSONObject jsonResponse = new JSONObject((String) entity.getBody());
    String accessToken = jsonResponse.get("access_token").toString();
    String refreshToken = jsonResponse.get("refresh_token").toString();

    log.info("Access token received: " + accessToken);
    log.info("Refresh token received: " + refreshToken);

    TokenValidationResult validationResult =
        tokenValidator.validate(
            TokenValidationRequest.of().idToken(idToken).accessToken(accessToken).build());

    if (validationResult.isValid()) {
      String url =
          new StringBuilder(state)
              .append("?id_token=" + idToken)
              .append("&access_token=" + accessToken)
              .append("&refresh_token=" + refreshToken)
              .toString();

      log.info("Redirecting to " + url);
      response.sendRedirect(url);
    } else {
      throw new ValidationException(
          TOKEN_VALIDATION_FAILED_MESSAGE + validationResult.getValidationFailureReason());
    }
  }

  @PostMapping("/refresh")
  public void refresh(@RequestBody RefreshBody refreshBody, HttpServletResponse response)
      throws JSONException, ValidationException, IOException {

    TenantConfiguration configuration =
        tenantService.loadTenant(Optional.ofNullable(refreshBody.getTenantId()));

    RefreshRequest refreshRequest =
        requestHandler.getRequestManager(configuration.getProvider()).initRefreshRequest();
    refreshRequest.setBaseUrl(configuration.getTokenUrl());
    refreshRequest.setScope(configuration.getAuthUrlScope());
    refreshRequest.setRedirectUrl(configuration.getRedirectUrl());
    refreshRequest.setClientId(configuration.getClientId());
    refreshRequest.setClientSecret(configuration.getClientSecret());

    refreshRequest.setRefreshToken(refreshBody.getRefreshToken());
    refreshRequest.setGrantType(refreshBody.getGrantType());

    ResponseEntity entity = refreshRequest.execute();

    JSONObject jsonResponse = new JSONObject((String) entity.getBody());
    String idToken = jsonResponse.get("id_token").toString();
    String newAccessToken = jsonResponse.get("access_token").toString();
    String newRefreshToken = jsonResponse.get("refresh_token").toString();

    log.info("New access token received: " + newAccessToken);
    log.info("New refresh token received: " + newRefreshToken);

    TokenValidationResult validationResult =
        tokenValidator.validate(
            TokenValidationRequest.of().idToken(idToken).accessToken(newAccessToken).build());

    if (validationResult.isValid()) {
      String url =
          new StringBuilder(refreshBody.getState())
              .append("?id_token=" + idToken)
              .append("&access_token=" + newAccessToken)
              .append("&refresh_token=" + newRefreshToken)
              .toString();

      log.info("Redirecting to " + url);
      response.sendRedirect(url);
    } else {
      throw new ValidationException(
          TOKEN_VALIDATION_FAILED_MESSAGE + validationResult.getValidationFailureReason());
    }
  }

  @GetMapping("/logout")
  public void logoutPage(
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "state", required = false) String state,
      HttpServletResponse response) {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));

    LogoutRequest request =
        requestHandler.getRequestManager(configuration.getProvider()).initLogoutRequest();
    request.setBaseUrl(configuration.getLogoutUrl());
    request.setRedirectUrl(state);
    request.execute(response);
  }

  /**
   * Token validation endpoint
   *
   * @param request validation request wrapper
   * @return HTTP Response
   */
  @PostMapping(value = "/verify_token")
  public ResponseEntity verifyToken(@RequestBody TokenValidationRequest request) {
    TokenValidationResult validationResult = tokenValidator.validate(request);

    if (validationResult.isValid()) return ResponseEntity.ok(TOKEN_VALID_MESSAGE);
    else
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
          .body(TOKEN_VALIDATION_FAILED_MESSAGE + validationResult.getValidationFailureReason());
  }
}
