/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Jul 26, 2019
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
  @Operation(
      summary = "Gets single User",
      tags = "users",
      description = "Gets single user by the ID",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Requested user representation",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserInfoDTO.class)))
      })
  public ResponseEntity<UserInfoDTO> getUser(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @Parameter(description = "ID of selected user") @PathVariable String userId,
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
  @Operation(
      description = "Gets groups which given user belongs to",
      tags = "users",
      summary = "Gets groups which given user belongs to",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Array of groups which given user belongs to",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = UserGroupDTO.class))))
      })
  public ResponseEntity<Set<UserGroupDTO>> getGroupsUserBelongsTo(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @Parameter(description = "ID of selected user") @PathVariable String userId,
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
  @Operation(
      description = "Updates user information in identity provider",
      tags = "users",
      summary = "Updates user information in identity provider",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "User to be updated",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = UserInfoDTO.class))),
      responses = {@ApiResponse(responseCode = "204")})
  public ResponseEntity updateUser(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @Parameter(description = "ID of selected user") @PathVariable String userId,
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
