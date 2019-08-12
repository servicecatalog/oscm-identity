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
import org.oscm.identity.oidc.request.DefaultGetUserRequest;
import org.oscm.identity.oidc.request.RequestHandler;
import org.oscm.identity.oidc.request.UserRequest;
import org.oscm.identity.oidc.response.ResponseHandler;
import org.oscm.identity.oidc.response.UserInfoResponse;
import org.oscm.identity.oidc.response.mapper.ResponseMapper;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
   * Retrieves user information from identity provider
   *
   * @param userId id of the user
   * @param tenantId id of the tenant defining identity provider
   * @param token bearer idToken for accessing identity provider related API
   * @return HTTP Response object
   * @throws JSONException
   */
  @GetMapping("/users/{userId}")
  public ResponseEntity<UserInfoResponse> getUser(
      @PathVariable String userId,
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "token") String token)
      throws JSONException {

    TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));

    UserRequest request =
        requestHandler.getRequestManager(configuration.getProvider()).initGetUserRequest();
    request.setBaseUrl(configuration.getUsersEndpoint());
    request.setToken(token);

    if (request instanceof DefaultGetUserRequest) {

      DefaultGetUserRequest defaultRequest = (DefaultGetUserRequest) request;
      defaultRequest.setUserId(userId);
      defaultRequest.setSelect("givenName,surname,mail,businessPhones,country,city,streetAddress,postalCode");
    }

    ResponseEntity<String> response = request.execute();
    JSONObject jsonResponse = new JSONObject(response.getBody());

    ResponseMapper mapper = ResponseHandler.getResponseMapper(configuration.getProvider());
    UserInfoResponse userInfoResponse = mapper.getUserResponse(jsonResponse);

    return ResponseEntity.ok(userInfoResponse);
  }
}
