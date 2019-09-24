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
import org.oscm.identity.error.InvalidRequestException;
import org.oscm.identity.model.json.UserGroupDTO;
import org.oscm.identity.model.json.UserInfo;
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

/** Controller class for handling incoming group related requests */
@Slf4j
@RestController
public class GroupController {

  private TenantService tenantService;
  private RequestHandler requestHandler;
  private UserController userController;

  @Autowired
  public GroupController(
      TenantService tenantService, RequestHandler requestHandler, UserController userController) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
    this.userController = userController;
  }

  /**
   * Represents the endpoint for creating the user group in identity provider
   *
   * @param tenantId id of the tenant defining identity provider
   * @param bearerToken token included in authorization header
   * @param userGroupRequest body of the request
   * @return http response with json representation of created user group
   * @throws JSONException
   */
  @PostMapping("/tenants/{tenantId}/groups")
  public ResponseEntity<UserGroupDTO> createGroup(
      @PathVariable String tenantId,
      @RequestHeader(value = "Authorization") String bearerToken,
      @RequestBody UserGroupDTO userGroupRequest)
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
    UserGroupDTO userGroupResponse = mapper.getUserGroup(jsonResponse);

    return ResponseEntity.status(201).body(userGroupResponse);
  }

  /**
   * Represents the endpoint for assigning new member to the user group in identity provider.
   * Returns bad request (400) response if member is already assigned to any user group
   *
   * @param tenantId id of the tenant defining identity provider
   * @param groupId id of the group existing in identity provider
   * @param bearerToken token included in authorization header
   * @param userInfo json representation of new member
   * @return http response with no content (204)
   * @throws JSONException
   */
  @PostMapping("/tenants/{tenantId}/groups/{groupId}/members")
  public ResponseEntity addMember(
      @PathVariable String tenantId,
      @PathVariable String groupId,
      @RequestHeader(value = "Authorization") String bearerToken,
      @RequestBody @Valid UserInfo userInfo)
      throws JSONException {

    ResponseEntity<Set<UserGroupDTO>> groups =
        userController.getGroupsUserBelongsTo(tenantId, userInfo.getUserId(), bearerToken);

    if (groups.getBody().size() > 0) {
      throw new InvalidRequestException(
          "User " + userInfo.getUserId() + " is already member of one of the groups");
    }

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    GroupRequest groupRequest =
        requestHandler.getRequestManager(provider).initAddGroupMemberRequest();
    groupRequest.setBaseUrl(configuration.getGroupsEndpoint());
    groupRequest.setToken(token);

    if (groupRequest instanceof DefaultAddGroupMemberRequest) {
      DefaultAddGroupMemberRequest request = (DefaultAddGroupMemberRequest) groupRequest;
      request.setUserId(userInfo.getUserId());
      request.setGroupId(groupId);
    }

    groupRequest.execute();
    return ResponseEntity.noContent().build();
  }

  /**
   * @param tenantId id of the tenant defining identity provider
   * @param groupId id of the group existing in identity provider
   * @param bearerToken token included in authorization header
   * @return http response with json representation of retrieved group members
   * @throws JSONException
   */
  @GetMapping("/tenants/{tenantId}/groups/{groupId}/members")
  public ResponseEntity getMembers(
      @PathVariable String tenantId,
      @PathVariable String groupId,
      @RequestHeader(value = "Authorization") String bearerToken)
      throws JSONException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    GroupRequest groupRequest =
        requestHandler.getRequestManager(provider).initGetGroupMembersRequest();
    groupRequest.setBaseUrl(configuration.getGroupsEndpoint());
    groupRequest.setToken(token);

    if (groupRequest instanceof DefaultGetGroupMembersRequest) {
      DefaultGetGroupMembersRequest request = (DefaultGetGroupMembersRequest) groupRequest;
      request.setGroupId(groupId);
      request.setSelect(
          "userPrincipalName,givenName,surname,mail,businessPhones,country,city,streetAddress,postalCode");
    }

    ResponseEntity<String> response = groupRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    Set<UserInfo> users = mapper.getGroupMembers(jsonResponse);

    return ResponseEntity.ok(users);
  }

  /**
   * @param tenantId id of the tenant defining identity provider
   * @param bearerToken token included in authorization header
   * @return http response with json representation of retrieved groups
   * @throws JSONException
   */
  @GetMapping("/tenants/{tenantId}/groups")
  public ResponseEntity getGroups(
      @PathVariable String tenantId, @RequestHeader(value = "Authorization") String bearerToken)
      throws JSONException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    GroupRequest groupRequest = requestHandler.getRequestManager(provider).initGetGroupsRequest();
    groupRequest.setBaseUrl(configuration.getGroupsEndpoint());
    groupRequest.setToken(token);

    ResponseEntity<String> response = groupRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    Set<UserGroupDTO> groups = mapper.getGroups(jsonResponse);

    return ResponseEntity.ok(groups);
  }
}
