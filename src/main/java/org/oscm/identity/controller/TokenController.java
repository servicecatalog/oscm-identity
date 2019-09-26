/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Sep 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.json.AccessToken;
import org.oscm.identity.model.json.RefreshTokenDTO;
import org.oscm.identity.model.json.TokenDetailsDTO;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.TokenValidationFlow;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/** Controller class for handling incoming token related requests */
@Slf4j
@RestController
public class TokenController {

  private static final String TOKEN_VALID_MESSAGE = "Token valid!";

  private TenantService tenantService;
  private RequestHandler requestHandler;
  private TokenValidationFlow validationFlow;

  @Autowired
  public TokenController(TenantService tenantService,
          RequestHandler requestHandler,
          TokenValidationFlow validationFlow) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
    this.validationFlow = validationFlow;
  }

  /**
   * Represents the endpoint for retrieving the access token from external identity provider
   * according to client credentials grant flow
   *
   * @param tenantId id of the tenant defining identity provider
   * @return http response object containing json representing the access token
   * @throws JSONException
   */
  @PostMapping("tenants/{tenantId}/token")
  public ResponseEntity getAccessToken(@PathVariable String tenantId) throws JSONException {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();
    String scope =
        new StringBuilder(configuration.getUriAppId()).append("/").append(".default").toString();

    TokenRequest tokenRequest = requestHandler.getRequestManager(provider).initTokenRequest();
    tokenRequest.setBaseUrl(configuration.getTokenUrl());
    tokenRequest.setClientId(configuration.getClientId());
    tokenRequest.setClientSecret(configuration.getClientSecret());
    tokenRequest.setScope(scope);
    tokenRequest.setGrantType("client_credentials");

    ResponseEntity<String> response = tokenRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    AccessToken accessToken = mapper.getAccessToken(jsonResponse);

    return ResponseEntity.ok(accessToken);
  }

  @PostMapping("/tenants/{tenantId}/token/refresh")
  public ResponseEntity refreshAccessToken(
      @PathVariable String tenantId, @RequestBody RefreshTokenDTO refreshRequest)
      throws JSONException {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    TokenRequest refreshTokenRequest =
        requestHandler.getRequestManager(provider).initTokenRequest();
    refreshTokenRequest.setBaseUrl(configuration.getTokenUrl());
    refreshTokenRequest.setScope(configuration.getAuthUrlScope());
    refreshTokenRequest.setClientId(configuration.getClientId());
    refreshTokenRequest.setClientSecret(configuration.getClientSecret());
    refreshTokenRequest.setRefreshToken(refreshRequest.getRefreshToken());
    refreshTokenRequest.setGrantType("refresh_token");

    ResponseEntity<String> response = refreshTokenRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    RefreshTokenDTO refreshResponse = mapper.getRefreshToken(jsonResponse);

    return ResponseEntity.ok(refreshResponse);
  }

  /**
   * Token validation endpoint
   *
   * @param tenantId ID of the tenant for which configuration will be loaded
   * @param request token details wrapper
   * @return HTTP Response
   */
  @PostMapping("/tenants/{tenantId}/token/verify")
  public ResponseEntity verifyToken(
      @PathVariable String tenantId, @RequestBody TokenDetailsDTO request)
      throws TokenValidationException {
    validationFlow.forTenantOf(tenantId).withTokenFrom(request).validate();
    return ResponseEntity.ok(TOKEN_VALID_MESSAGE);
  }
}
