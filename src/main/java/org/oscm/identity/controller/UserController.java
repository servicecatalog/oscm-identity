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
import org.oscm.identity.model.json.UserGroupDTO;
import org.oscm.identity.model.json.UserInfoDTO;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.*;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
   * @param userId userId of the user
   * @param tenantId userId of the tenant defining identity provider
   * @param bearerToken token included in authorization header
   * @return http response object containing json representing the user
   * @throws JSONException
   */
  @GetMapping("/tenants/{tenantId}/users/{userId}")
  public ResponseEntity<UserInfoDTO> getUser(
      @PathVariable String tenantId,
      @PathVariable String userId,
      @RequestHeader(value = "Authorization") String bearerToken)
      throws JSONException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    UserRequest request = requestHandler.getRequestManager(provider).initGetUserRequest();
    request.setBaseUrl(configuration.getUsersEndpoint());
    request.setToken(token);

    if (request instanceof DefaultGetUserRequest) {
      DefaultGetUserRequest defaultRequest = (DefaultGetUserRequest) request;
      defaultRequest.setUserId(userId);
      defaultRequest.setSelect(
          "userPrincipalName,givenName,surname,mail,businessPhones,country,city,streetAddress,postalCode,otherMails");
    }

    ResponseEntity<String> response = request.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    UserInfoDTO userInfoDTO = mapper.getUserInfo(jsonResponse);

    return ResponseEntity.ok(userInfoDTO);
  }

  /**
   * Represents the endpoint for getting groups which given user belongs to
   *
   * @param userId userId of the user
   * @param tenantId userId of the tenant defining identity provider
   * @param bearerToken token included in authorization header
   * @return http response with json array of groups which user belongs to
   * @throws JSONException
   */
  @GetMapping("/tenants/{tenantId}/users/{userId}/groups")
  public ResponseEntity<Set<UserGroupDTO>> getGroupsUserBelongsTo(
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
    Set<UserGroupDTO> groups = mapper.getGroupsUserBelongsTo(jsonResponse);

    return ResponseEntity.ok(groups);
  }

  /**
   * Represents the endpoint for updating user information in identity provider
   *
   * @param userId userId of the user
   * @param tenantId userId of the tenant defining identity provider
   * @param bearerToken token included in authorization header
   * @param userInfo request body including values to be updated
   * @return http response with no content
   * @throws JSONException
   */
  @PutMapping("/tenants/{tenantId}/users/{userId}")
  public ResponseEntity updateUser(
      @PathVariable String tenantId,
      @PathVariable String userId,
      @RequestHeader(value = "Authorization") String bearerToken,
      @RequestBody UserInfoDTO userInfo)
      throws JSONException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    UserRequest userRequest = requestHandler.getRequestManager(provider).initUpdateUserRequest();
    userRequest.setBaseUrl(configuration.getUsersEndpoint());
    userRequest.setToken(token);

    if (userRequest instanceof DefaultUpdateUserRequest) {
      DefaultUpdateUserRequest request = (DefaultUpdateUserRequest) userRequest;
      request.setUserId(userId);
      request.setEmail(userInfo.getEmail());
    }

    userRequest.execute();
    return ResponseEntity.noContent().build();
  }
}
