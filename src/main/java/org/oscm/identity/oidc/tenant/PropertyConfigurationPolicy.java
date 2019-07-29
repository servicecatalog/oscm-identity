/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: June 13, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class PropertyConfigurationPolicy implements TenantConfigurationPolicy {

  private TenantPropertyFileRetriever propertyFileRetriever;

  @Autowired
  public PropertyConfigurationPolicy(TenantPropertyFileRetriever propertyFileRetriever) {
    this.propertyFileRetriever = propertyFileRetriever;
  }

  @Override
  public TenantConfiguration loadTenant(String tenantId) {
    Properties properties = propertyFileRetriever.retrievePropertiesFor(tenantId);

    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setTenantId(tenantId);
    configuration.setProvider(properties.getProperty("oidc.provider"));
    configuration.setAuthUrl(properties.getProperty("oidc.authUrl"));
    configuration.setClientId(properties.getProperty("oidc.clientId"));
    configuration.setIdTokenRedirectUrl(properties.getProperty("oidc.idTokenRedirectUrl"));
    configuration.setOidConfigUrl(properties.getProperty("oidc.openidConfigurationUrl"));

    return configuration;
  }
}
