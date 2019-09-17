/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Sep 16, 2019
 *
 *******************************************************************************/

package org.oscm.identity.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.AccessToken;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/** Controller class for handling incoming token related requests */
@Slf4j
@RestController
public class TokenController {

  private TenantService tenantService;
  private RequestHandler requestHandler;

  @Autowired
  public TokenController(TenantService tenantService, RequestHandler requestHandler) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
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
}
