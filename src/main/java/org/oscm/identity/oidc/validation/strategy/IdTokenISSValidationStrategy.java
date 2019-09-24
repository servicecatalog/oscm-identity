/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: Jul 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IdTokenISSValidationStrategy extends TokenValidationStrategy {

  private TenantService tenantService;
  private RestTemplate restTemplate;

  @Autowired
  public IdTokenISSValidationStrategy(TenantService tenantService, RestTemplate restTemplate) {
    this.tenantService = tenantService;
    this.restTemplate = restTemplate;
  }

  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws IdTokenValidationException {
    // FIXME: Issuer for access token is the same as in v1 tokens, even though
    // FIXME: Id token is in version 2 - possible bug?
    try {
      String issuer = getIssuerFromRemoteConfig(tenantConfiguration.getConfigurationUrl());
      if (!issuer.equals(decodedToken.getIssuer()))
        throw new IdTokenValidationException(getFailureMessage());
    } catch (JSONException e) {
      throw new IdTokenValidationException(e.getMessage());
    }
  }

  @Override
  public String getFailureMessage() {
    return "Issuer values from OID config and OID IdToken does not match";
  }

  /**
   * Extracts <i>issuer param</i> from remote OpenID configuration
   *
   * @param oidConfigUrl URL to OpenID configuration JSON
   * @return issuer parameter
   * @throws JSONException
   */
  private String getIssuerFromRemoteConfig(String oidConfigUrl) throws JSONException {
    String responseJSON = restTemplate.getForObject(oidConfigUrl, String.class);
    return new JSONObject(responseJSON).get("issuer").toString();
  }
}
