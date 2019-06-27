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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.TenantConfigurationException;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PropertyConfigurationPolicyTest {

  @Spy private PropertyConfigurationPolicy configurationPolicy;

  @ParameterizedTest
  @ValueSource(strings = {"default", "test"})
  public void shouldLoadTenant_givenTenantOf(String tenantId) throws FileNotFoundException {

    InputStream stream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("tenant-" + tenantId + ".properties");
    doReturn(stream).when(configurationPolicy).getFileInputSteam(anyString());

    TenantConfiguration configuration = configurationPolicy.loadTenant(tenantId);

    assertThat(configuration)
        .extracting(TenantConfiguration::getProvider)
        .isEqualTo(tenantId + "Provider");
    assertThat(configuration).extracting(TenantConfiguration::getTenantId).isEqualTo(tenantId);
    assertThat(configuration)
        .extracting(TenantConfiguration::getClientId)
        .isEqualTo(tenantId + "ClientId");
    assertThat(configuration)
        .extracting(TenantConfiguration::getAuthUrl)
        .isEqualTo(tenantId + "AuthUrl");
  }

  @Test
  public void shouldThrowException_givenNotConfiguredTenant() {
    assertThatExceptionOfType(TenantConfigurationException.class)
        .isThrownBy(() -> configurationPolicy.loadTenant("not-configured-tenant"))
        .withMessageContaining("could not be loaded");
  }
}
