/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Aug 7, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.InvalidRequestException;
import org.oscm.identity.error.ResourceNotFoundException;
import org.oscm.identity.model.json.UserGroupDTO;
import org.oscm.identity.model.json.UserInfoDTO;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.*;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
   * @param tenantId id of the tenant defining identity provider
   * @param encodedGroupName - the URL encoded group name
   * @param bearerToken token included in authorization header
   * @param logErrors specifies whether exceptions should be logged. This value is configured inside
   *     globally in 'application.properties'. Default value for this param is 'true'.
   * @return http response with json representation of retrieved groups
   * @throws JSONException
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping("/tenants/{tenantId}/groups/{encodedGroupName}")
  @Operation(
      summary = "Gets group by its name for selected tenant",
      tags = {"groups"},
      description = "Gets group by its name for selected tenant",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Group of selected name",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserGroupDTO.class)))
      })
  @ResponseBody
  public ResponseEntity getGroup(
      @Parameter(description = "ID of the selected tenant") @PathVariable String tenantId,
      @Parameter(description = "Name of the group to get") @PathVariable String encodedGroupName,
      @Parameter(description = "Specifies whether exceptions should be logged")
          @RequestParam(value = "logErrors", required = false, defaultValue = "true")
          Boolean logErrors,
      @RequestHeader(value = "Authorization") String bearerToken)
      throws JSONException, ResourceNotFoundException {

    String token = requestHandler.getTokenOutOfAuthHeader(bearerToken);
    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    GroupRequest groupRequest = requestHandler.getRequestManager(provider).initGetGroupsRequest();
    groupRequest.setBaseUrl(configuration.getGroupsEndpoint());
    groupRequest.setToken(token);

    ResponseEntity<String> response = groupRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    String groupName = encodedGroupName;
    try {
      groupName = URLDecoder.decode(encodedGroupName, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    UserGroupDTO group = mapper.getGroup(jsonResponse, groupName, logErrors);
    return ResponseEntity.ok(group);
  }

  /**
   * @param tenantId - id of the tenant defining identity provider
   * @param bearerToken - token included in authorization header
   * @return HTTP response with JSON representation of retrieved groups
   * @throws JSONException
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping("/tenants/{tenantId}/groups")
  @Operation(
      summary = "Gets all groups for selected tenant",
      tags = "groups",
      description = "Gets all groups for selected tenant",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Group array for selected tenant",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = UserGroupDTO.class))))
      })
  public ResponseEntity getGroups(
      @Parameter(description = "ID of the selected tenant") @PathVariable String tenantId,
      @RequestHeader(value = "Authorization") String bearerToken)
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

  /**
   * Represents the endpoint for creating the user group in identity provider
   *
   * @param tenantId - id of the tenant defining identity provider
   * @param bearerToken - token included in authorization header
   * @param userGroupRequest - body of the request
   * @return HTTP response with JSON representation of created user group
   * @throws JSONException
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/tenants/{tenantId}/groups")
  @Operation(
      summary = "Creates group for selected tenant",
      tags = "groups",
      description = "Creates group for selected tenant",
      responses = @ApiResponse(responseCode = "201", description = "Group has been created"),
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = UserGroupDTO.class))))
  public ResponseEntity<UserGroupDTO> createGroup(
      @Parameter(description = "ID of the selected tenant") @PathVariable String tenantId,
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
   * @param userInfo JSON representation of new member
   * @return HTTP response with no content (204)
   * @throws JSONException
   */
  @SuppressWarnings("rawtypes")
  @PostMapping("/tenants/{tenantId}/groups/{groupId}/members")
  @Operation(
      summary = "Adds member to the group",
      tags = "groups",
      description = "Adds member to the group that belongs to selected tenant",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Member to be added",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = UserInfoDTO.class))),
      responses = {@ApiResponse(responseCode = "204")})
  public ResponseEntity addMember(
      @Parameter(description = "ID of the selected tenant") @PathVariable String tenantId,
      @Parameter(description = "ID of the selected group") @PathVariable String groupId,
      @RequestHeader(value = "Authorization") String bearerToken,
      @RequestBody @Valid UserInfoDTO userInfo)
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
   * @return HTTP response with JSON representation of retrieved group members
   * @throws JSONException
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  @GetMapping("/tenants/{tenantId}/groups/{groupId}/members")
  @Operation(
      summary = "Gets all members for selected group",
      tags = "groups",
      description = "Gets all members that belongs to selected group, assigned for selected tenant",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Member array for selected tenant",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = UserInfoDTO.class))))
      })
  public ResponseEntity getMembers(
      @Parameter(description = "ID of the selected tenant") @PathVariable String tenantId,
      @Parameter(description = "ID of the selected group") @PathVariable String groupId,
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
          "userPrincipalName,givenName,surname,mail,businessPhones,country,city,streetAddress,postalCode,otherMails");
    }

    ResponseEntity<String> response = groupRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    Set<UserInfoDTO> users = mapper.getGroupMembers(jsonResponse);

    return ResponseEntity.ok(users);
  }
}
