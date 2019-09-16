/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: June 26, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.tenant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.oscm.identity.error.TenantConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
public class PropertyConfigurationPolicyTest {

  @Autowired private PropertyConfigurationPolicy configurationPolicy;
  @Autowired private TenantPropertyFileRetriever propertyFileRetriever;

  @ParameterizedTest
  @ValueSource(strings = {"default", "test"})
  public void shouldLoadTenant_givenTenantOf(String tenantId) throws FileNotFoundException {
    InputStream stream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("tenant-" + tenantId + ".properties");

    TenantConfiguration configuration = configurationPolicy.loadTenant(tenantId);

    assertThat(configuration)
        .extracting(TenantConfiguration::getProvider)
        .isEqualTo(tenantId + "Provider");
    assertThat(configuration).extracting(TenantConfiguration::getTenantId).isEqualTo(tenantId);
    assertThat(configuration)
        .extracting(TenantConfiguration::getClientId)
        .isEqualTo(tenantId + "ClientId");
    assertThat(configuration)
            .extracting(TenantConfiguration::getClientSecret)
            .isEqualTo(tenantId + "ClientSecret");
    assertThat(configuration)
        .extracting(TenantConfiguration::getAuthUrl)
        .isEqualTo(tenantId + "AuthUrl");
    assertThat(configuration)
            .extracting(TenantConfiguration::getAuthUrlScope)
            .isEqualTo(tenantId + "AuthUrlScope");
    assertThat(configuration)
        .extracting(TenantConfiguration::getLogoutUrl)
        .isEqualTo(tenantId + "LogoutUrl");
    assertThat(configuration)
            .extracting(TenantConfiguration::getTokenUrl)
            .isEqualTo(tenantId + "TokenUrl");
    assertThat(configuration)
            .extracting(TenantConfiguration::getRedirectUrl)
            .isEqualTo(tenantId + "RedirectUrl");
    assertThat(configuration)
            .extracting(TenantConfiguration::getConfigurationUrl)
            .isEqualTo(tenantId + "ConfigurationUrl");
    assertThat(configuration)
            .extracting(TenantConfiguration::getUsersEndpoint)
            .isEqualTo(tenantId + "UsersEndpoint");
    assertThat(configuration)
            .extracting(TenantConfiguration::getNonce)
            .isEqualTo(tenantId + "Nonce");
  }

  @Test
  public void shouldThrowException_givenNotConfiguredTenant() {
    assertThatExceptionOfType(TenantConfigurationException.class)
        .isThrownBy(() -> configurationPolicy.loadTenant("not-configured-tenant"))
        .withMessageContaining("could not be loaded");
  }
}
