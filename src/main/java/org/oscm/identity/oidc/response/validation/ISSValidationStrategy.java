/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 19, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.response.validation;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Slf4j
@Component
public class ISSValidationStrategy implements TokenValidationStrategy {

  private TenantService tenantService;
  private RestTemplate restTemplate;

  @Autowired
  public ISSValidationStrategy(TenantService tenantService,
          RestTemplate restTemplate) {
    this.tenantService = tenantService;
    this.restTemplate = restTemplate;
  }

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    TenantConfiguration tenantConfiguration =
        tenantService.loadTenant(Optional.ofNullable(request.getTenantId()));

    try {
      String issuer = getIssuerFromRemoteConfig(tenantConfiguration.getOidConfigUrl());
      if (!issuer.equals(request.getDecodedToken().getIssuer()))
        throw new ValidationException(getFailureMessage());
    } catch (JSONException e) {
      throw new ValidationException(e.getMessage());
    }
  }

  @Override
  public String getFailureMessage() {
    return "Issuer values from OID config and OID token does not match";
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
