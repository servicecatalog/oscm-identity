/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Aug 7, 2019
 *
 *******************************************************************************/

package org.oscm.identity.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.json.UserGroup;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.DefaultCreateGroupRequest;
import org.oscm.identity.oidc.request.GroupRequest;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Controller class for handling incoming group related requests */
@Slf4j
@RestController
public class GroupController {

  private TenantService tenantService;
  private RequestHandler requestHandler;

  @Autowired
  public GroupController(TenantService tenantService, RequestHandler requestHandler) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
  }

  /**
   * Represents the endpoint for creating the user group in identity provider
   *
   * @param tenantId id of the tenant defining identity provider
   * @param bearerToken token included in authorization header
   * @param userGroupRequest body of the request
   * @return json representation of created user group
   * @throws JSONException
   */
  @PostMapping("/tenants/{tenantId}/groups")
  public ResponseEntity createGroup(
      @PathVariable String tenantId,
      @RequestHeader(value = "Authorization") String bearerToken,
      @RequestBody UserGroup userGroupRequest)
      throws JSONException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    GroupRequest groupRequest = requestHandler.getRequestManager(provider).initCreateGroupRequest();
    groupRequest.setBaseUrl(configuration.getGroupsEndpoint());
    groupRequest.setToken(token);

    if (groupRequest instanceof DefaultCreateGroupRequest) {
      DefaultCreateGroupRequest defaultRequest = (DefaultCreateGroupRequest) groupRequest;
      defaultRequest.setDisplayName(userGroupRequest.getName());
      defaultRequest.setDescription(userGroupRequest.getDescription());
    }

    ResponseEntity<String> response = groupRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    UserGroup userGroupResponse = mapper.getUserGroup(jsonResponse);

    return ResponseEntity.status(201).body(userGroupResponse);
  }
}
