/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 18, 2019
 *
 *******************************************************************************/
package org.oscm.identity.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.oidc.request.*;
import org.oscm.identity.oidc.response.UserInfoResponse;
import org.oscm.identity.oidc.response.validation.AuthTokenValidator;
import org.oscm.identity.oidc.response.validation.TokenValidationResult;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
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

    AuthorizationRequest request =
        RequestHandler.getRequestManager(configuration.getProvider()).initAuthorizationRequest();
    request.setBaseUrl(configuration.getAuthUrl());
    request.setClientId(configuration.getClientId());
    request.setRedirectUrl(configuration.getIdTokenRedirectUrl());
    request.setScope("openid offline_access https://graph.microsoft.com/user.read.all");
    request.setResponseType("code id_token");
    request.setResponseMode("form_post");
    // TODO: create nonce which should be validated for id_token
    request.setNonce("test-nonce");
    request.setState(state);

    request.execute(response);
  }

  @PostMapping("/id_token")
  public ModelAndView tokenCallback(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription,
      HttpServletResponse response)
          throws IOException, ValidationException, JSONException {

    if (error != null) {
      throw new IdentityProviderException(error + ": " + errorDescription);
    }

    log.info("Code:"+code);
    //TODO: get tenant out of state
    String tenantId = null;
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));

    TokenRequest tokenRequest = RequestHandler.getRequestManager(configuration.getProvider()).initTokenRequest();
    tokenRequest.setBaseUrl(configuration.getTokenUrl());
    tokenRequest.setClientId(configuration.getClientId());
    tokenRequest.setClientSecret(configuration.getClientSecret());
    tokenRequest.setCode(code);
    tokenRequest.setGrantType("authorization_code");
    tokenRequest.setRedirectUrl(configuration.getIdTokenRedirectUrl());

    ResponseEntity<String> entity = tokenRequest.execute();

    JSONObject jsonResponse = new JSONObject(entity.getBody());

    String accessToken = jsonResponse.get("access_token").toString();
    String refreshToken = jsonResponse.get("refresh_token").toString();

    TokenValidationResult validationResult =
        tokenValidator.validate(TokenValidationRequest.of().token(accessToken).build());

    log.info("TOKEN VALID:" + validationResult.isValid());
    log.info("Token received:" + accessToken);
    // response.sendRedirect(state + "?id_token=" + idToken);

    ModelAndView view = new ModelAndView();

    if (accessToken != null) {
      DecodedJWT decodedToken = JWT.decode(accessToken);
      view.addObject("accessToken", accessToken);
      view.addObject("refreshToken", refreshToken);
      view.addObject("expirationDate", decodedToken.getExpiresAt());
      view.addObject("name", decodedToken.getClaim("name").asString());
      view.addObject("uniqueName", decodedToken.getClaim("unique_name").asString());
    }

    view.addObject("code", code);
    view.setViewName("idtoken");

    return view;
    /*} else {
      throw new ValidationException(
          TOKEN_VALIDATION_FAILED_MESSAGE + validationResult.getValidationFailureReason());
    }*/
  }

  @GetMapping("/logout")
  public void logoutPage(
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "state", required = false) String state,
      HttpServletResponse response) {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));

    LogoutRequest request =
        RequestHandler.getRequestManager(configuration.getProvider()).initLogoutRequest();

    request.execute(response);
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

  @GetMapping("/users/{userId}")
  public ResponseEntity<UserInfoResponse> getUser(
      @PathVariable String userId,
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "token") String token) {

    UserInfoResponse userInfo = new UserInfoResponse();

    return ResponseEntity.ok(userInfo);
  }


}
