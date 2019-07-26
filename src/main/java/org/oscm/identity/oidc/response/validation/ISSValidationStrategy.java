/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.oscm.identity.oidc.request.proxy.ProxyHandler;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.ValidationException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Optional;

@Slf4j
@Component
public class ISSValidationStrategy implements TokenValidationStrategy {

  private TenantService tenantService;
  private ProxyHandler proxyHandler;

  @Autowired
  public ISSValidationStrategy(TenantService tenantService, ProxyHandler proxyHandler) {
    this.tenantService = tenantService;
    this.proxyHandler = proxyHandler;
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

    RestTemplate restTemplate = new RestTemplate();

    if (Boolean.valueOf(proxyHandler.isRequired())) {
      SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
      Proxy proxy =
          new Proxy(
              Proxy.Type.HTTP,
              new InetSocketAddress(proxyHandler.getHost(), proxyHandler.getPort()));
      factory.setProxy(proxy);
      restTemplate.setRequestFactory(factory);
    }

    String responseJSON = restTemplate.getForObject(oidConfigUrl, String.class);
    return new JSONObject(responseJSON).get("issuer").toString();
  }
}
