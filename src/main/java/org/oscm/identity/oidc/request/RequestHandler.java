/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 25, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.request;

import org.apache.logging.log4j.util.Strings;
import org.oscm.identity.commons.AccessType;
import org.oscm.identity.error.IdentityProviderException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Simple handler for getting proper RequestManager instance */
@Component
public class RequestHandler {

  RestTemplate restTemplate;

  @Autowired
  public RequestHandler(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves RequestManager instance base on given provider
   *
   * @param provider external identity provider name
   * @return RequestManager instance
   */
  public RequestManager getRequestManager(String provider) {

    RequestManager requestManager;

    switch (provider) {
      case "default":
        requestManager = new DefaultRequestManager(this.restTemplate);
        break;
      default:
        throw new IdentityProviderException(
            "No request manager implementation for identity provider [" + provider + "]");
    }
    return requestManager;
  }

  /**
   * Retrieves the token value from authorization header containing bearer token.
   *
   * @param bearerToken header's bearer token
   * @return token value
   */
  public String getTokenOutOfAuthHeader(String bearerToken) {
    String token = bearerToken.replaceFirst("Bearer ", "").trim();
    return token;
  }

  /**
   * Appends state parameter with tenant information
   *
   * @param state state representing url to which identity service should go back after successful login
   * @param tenantId id of tenant
   * @return modified state parameter
   */
  public String appendStateWithTenantId(String state, String tenantId) {

    if (!Strings.isBlank(tenantId)) {
      state = new StringBuilder(state).append("?tenantId=").append(tenantId).toString();
    }
    return state;
  }

  /**
   * Retrieves tenant information of of the passed state parameter
   *
   * @param state state representing url to which identity service should go back after successful login
   * @return id of tenant or null if state does not contain it
   */
  public String getTenantIdFromState(String state) {

    int tenantParam = state.indexOf("tenantId=");

    if (tenantParam > 0) {
      return state.substring(tenantParam).split("=")[1];
    }
    return null;
  }

  /**
   * Cleans the state parameter so that it doe not contain tenant information
   *
   * @param state state representing url to which identity service should go back after successful login
   * @return modified state parameter
   */
  public String getStateWithoutTenant(String state) {

    int tenantParam = state.indexOf("tenantId=");

    if (tenantParam > 0) {
      state = state.substring(0, tenantParam - 1);
    }
    return state;
  }

  /**
   * Retrieves the scope for requesting access token with client credentials grant based on given
   * access type
   *
   * @param accessType type of access
   * @param configuration tenant's configuration
   * @return scope parameter
   */
  public String getScope(AccessType accessType, TenantConfiguration configuration) {
      return configuration.getIdpApiUri();
  }
}
