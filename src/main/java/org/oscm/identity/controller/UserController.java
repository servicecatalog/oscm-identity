/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/
package org.oscm.identity.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.json.UserInfo;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.DefaultGetUserGroupsRequest;
import org.oscm.identity.oidc.request.DefaultGetUserRequest;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.UserRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/** Controller class for handling incoming user related requests */
@Slf4j
@RestController
public class UserController {

  private TenantService tenantService;
  private RequestHandler requestHandler;

  @Autowired
  public UserController(TenantService tenantService, RequestHandler requestHandler) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
  }

  /**
   * Represents the endpoint for getting user information from identity provider
   *
   * @param userId id of the user
   * @param tenantId id of the tenant defining identity provider
   * @param token bearer idToken for accessing identity provider related API
   * @return HTTP Response object
   * @throws JSONException
   */
  @GetMapping("/users/{userId}")
  public ResponseEntity<UserInfo> getUser(
      @PathVariable String userId,
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "token") String token)
      throws JSONException {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    UserRequest request = requestHandler.getRequestManager(provider).initGetUserRequest();
    request.setBaseUrl(configuration.getUsersEndpoint());
    request.setToken(token);

    if (request instanceof DefaultGetUserRequest) {
      DefaultGetUserRequest defaultRequest = (DefaultGetUserRequest) request;
      defaultRequest.setUserId(userId);
      defaultRequest.setSelect(
          "givenName,surname,mail,businessPhones,country,city,streetAddress,postalCode");
    }

    ResponseEntity<String> response = request.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    UserInfo userInfo = mapper.getUserInfo(jsonResponse);

    return ResponseEntity.ok(userInfo);
  }

  @GetMapping("/tenants/{tenantId}/users/{userId}/groups")
  public ResponseEntity<Set<UserGroup>> getGroupsUserBelongsTo(
      @PathVariable String tenantId,
      @PathVariable String userId,
      @RequestHeader(value = "Authorization") String bearerToken)
      throws JSONException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    UserRequest userRequest = requestHandler.getRequestManager(provider).initGetUserGroupsRequest();
    userRequest.setBaseUrl(configuration.getUsersEndpoint());
    userRequest.setToken(token);

    if (userRequest instanceof DefaultGetUserGroupsRequest) {
      DefaultGetUserGroupsRequest request = (DefaultGetUserGroupsRequest) userRequest;
      request.setUserId(userId);
    }

    ResponseEntity<String> response = userRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    Set<UserGroup> groups = mapper.getGroupsUserBelongsTo(jsonResponse);

    return ResponseEntity.ok(groups);
  }
}
