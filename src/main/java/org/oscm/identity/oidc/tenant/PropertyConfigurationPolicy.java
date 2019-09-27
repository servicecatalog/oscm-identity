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
    configuration.setClientId(properties.getProperty("oidc.clientId"));
    configuration.setClientSecret(properties.getProperty("oidc.clientSecret"));
    configuration.setAuthUrl(properties.getProperty("oidc.authUrl"));
    configuration.setAuthUrlScope(properties.getProperty("oidc.authUrlScope"));
    configuration.setLogoutUrl(properties.getProperty("oidc.logoutUrl"));
    configuration.setTokenUrl(properties.getProperty("oidc.tokenUrl"));
    configuration.setRedirectUrl(properties.getProperty("oidc.redirectUrl"));
    configuration.setConfigurationUrl(properties.getProperty("oidc.configurationUrl"));
    configuration.setUsersEndpoint(properties.getProperty("oidc.usersEndpoint"));
    configuration.setGroupsEndpoint(properties.getProperty("oidc.groupsEndpoint"));
    configuration.setNonce(properties.getProperty("oidc.nonce"));
    configuration.setAppIdUri(properties.getProperty("oidc.appIdUri"));
    configuration.setIdpApiUri(properties.getProperty("oidc.idpApiUri"));

    return configuration;
  }
}
