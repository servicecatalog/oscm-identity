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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.commons.AccessType;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.json.*;
import org.oscm.identity.model.response.ResponseHandler;
import org.oscm.identity.model.response.ResponseMapper;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.TokenRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.TokenValidationFlow;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

  private TenantService tenantService;
  private RequestHandler requestHandler;
  private TokenValidationFlow validationFlow;

  @Autowired
  public TokenController(
      TenantService tenantService,
      RequestHandler requestHandler,
      TokenValidationFlow validationFlow) {
    this.tenantService = tenantService;
    this.requestHandler = requestHandler;
    this.validationFlow = validationFlow;
  }

  /**
   * Endpoint for retrieving the access token base on client credentials grant flow
   *
   * @param tenantId id of the tenant
   * @param token http request body containing type of access information
   * @return http response object containing access token
   * @throws JSONException
   */
  @PostMapping("tenants/{tenantId}/token")
  @Operation(
      summary = "Gets new access token",
      tags = "tokens",
      description = "Gets new access token for selected tenant",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Type of token to be returned",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = AccessTokenDTO.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Valid access token",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccessTokenDTO.class)))
      })
  public ResponseEntity getAccessToken(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @RequestBody AccessTokenDTO token)
      throws JSONException {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();
    AccessType accessType = token.getAccessType();

    String requestedScope = requestHandler.getScope(accessType, configuration);
    String scope = new StringBuilder(requestedScope).append("/").append(".default").toString();

    TokenRequest tokenRequest = requestHandler.getRequestManager(provider).initTokenRequest();
    tokenRequest.setBaseUrl(configuration.getTokenUrl());
    tokenRequest.setClientId(configuration.getClientId());
    tokenRequest.setClientSecret(configuration.getClientSecret());
    tokenRequest.setScope(scope);
    tokenRequest.setGrantType("client_credentials");

    ResponseEntity<String> response = tokenRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    AccessTokenDTO accessToken = mapper.getAccessToken(jsonResponse);
    accessToken.setAccessType(accessType);
    return ResponseEntity.ok(accessToken);
  }

  /**
   * Endpoint for refreshing expired access token
   *
   * @param tenantId id of the tenant
   * @param refreshRequest http request body containing refresh token
   * @return http response containing new refresh and access token
   * @throws JSONException
   */
  @PostMapping("/tenants/{tenantId}/token/refresh")
  @Operation(
      description = "Refreshes expired access token",
      tags = "tokens",
      summary = "Refreshes expired access token",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Valid refresh token",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = RefreshTokenDTO.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "New Access and Refresh token pair",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RefreshTokenDTO.class)))
      })
  public ResponseEntity refreshAccessToken(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @RequestBody RefreshTokenDTO refreshRequest)
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
   * Endpoint for token (both access token and id token) validation
   *
   * @param tenantId id of the tenant
   * @param request http request body containing token details
   * @return http response containing user id
   */
  @PostMapping("/tenants/{tenantId}/token/verify")
  @Operation(
      description = "Validates tokens",
      tags = "tokens",
      summary = "Validates tokens of selected type",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Token to validate",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = TokenDetailsDTO.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User ID for the user associated with validated token",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserIdDTO.class)))
      })
  public ResponseEntity verifyToken(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @RequestBody TokenDetailsDTO request)
      throws TokenValidationException {

    String username = validationFlow.forTenantOf(tenantId).withTokenFrom(request).validate();
    return ResponseEntity.ok(UserIdDTO.of().userId(username).build());
  }

  /**
   * Endpoint for getting id token based on resource owner password credentials grant flow
   *
   * @param tenantId id of tenant
   * @param credentials http request body containing IDP credentials
   * @return http response containing id token
   * @throws JSONException
   */
  @PostMapping("tenants/{tenantId}/token/identify")
  @Operation(
      description = "Gets id token based on resource owner password credentials grant flow",
      tags = "tokens",
      summary = "Gets id token based on resource owner password credentials grant flow",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "User credentials",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = CredentialsDTO.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Valid ID token",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = IdTokenDTO.class)))
      })
  public ResponseEntity getIdToken(
      @Parameter(description = "ID of selected tenant") @PathVariable String tenantId,
      @RequestBody CredentialsDTO credentials)
      throws JSONException {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
    String provider = configuration.getProvider();

    TokenRequest tokenRequest = requestHandler.getRequestManager(provider).initTokenRequest();
    tokenRequest.setBaseUrl(configuration.getTokenUrl());
    tokenRequest.setClientId(configuration.getClientId());
    tokenRequest.setClientSecret(configuration.getClientSecret());
    tokenRequest.setUsername(credentials.getUsername());
    tokenRequest.setPassword(credentials.getPassword());
    tokenRequest.setScope("openid profile");
    tokenRequest.setGrantType("password");

    ResponseEntity<String> response = tokenRequest.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(provider);
    IdTokenDTO idToken = mapper.getIdToken(jsonResponse);
    return ResponseEntity.ok(idToken);
  }
}
