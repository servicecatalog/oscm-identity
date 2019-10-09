/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jun 19, 2019
 *
 *******************************************************************************/
package org.oscm.identity.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.oidc.request.AuthorizationRequest;
import org.oscm.identity.oidc.request.LogoutRequest;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
public class MainController {

  private TenantService tenantService;
  private RequestHandler requestHandler;

  @Autowired
  public MainController(
      TenantService tenantService,
      RequestHandler requestHandler) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
  }

  @GetMapping
  public String homePage() {
    return "Welcome to the oscm-identity home page";
  }

  @GetMapping("/login")
  public void loginPage(
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "state") String state,
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
    request.setState(requestHandler.appendStateWithTenantId(state, tenantId));
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
      throws IOException, JSONException {

    if (error != null) {
      throw new IdentityProviderException(error + ": " + errorDescription);
    }

    String tenantId = requestHandler.getTenantIdFromState(state);
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
    log.info("Id token received: " + idToken);

    String url =
        new StringBuilder(requestHandler.getStateWithoutTenant(state))
            .append("?id_token=" + idToken)
            .append("&access_token=" + accessToken)
            .append("&refresh_token=" + refreshToken)
            .toString();

    log.info("Redirecting to " + url);
    response.sendRedirect(url);
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
}
