/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 19, 2019
 *
 *******************************************************************************/

package org.oscm.identity.oidc.validation.strategy;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Slf4j
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
  public void execute(TokenValidationRequest request) throws ValidationException {
    TenantConfiguration tenantConfiguration =
        tenantService.loadTenant(Optional.ofNullable(request.getTenantId()));

    if(request.getDecodedIdToken() == null) {
      logIDTokenNotFound(this);
      return;
    }
    //FIXME: Issuer for access token is the same as in v1 tokens, even though
    //FIXME: Id token is in version 2 - possible bug?
    try {
      String issuer = getIssuerFromRemoteConfig(tenantConfiguration.getConfigurationUrl());
      if (!issuer.equals(request.getDecodedIdToken().getIssuer()))
        throw new ValidationException(getFailureMessage());
    } catch (JSONException e) {
      throw new ValidationException(e.getMessage());
    }
  }

  @Override
  public String getFailureMessage() {
    return "Issuer values from OID config and OID idToken does not match";
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
