package org.oscm.identity.oidc.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oscm.identity.error.TenantConfigurationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class TenantConfigurationValidatorTest {

  public TenantConfigurationValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new TenantConfigurationValidator();
  }

  @Test
  public void shouldValidateConfiguration_givenValidConfiguration() {
    // GIVEN
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setAppIdUri("appIdUri");
    configuration.setClientId("clientId");
    configuration.setConfigurationUrl("configUrl");
    configuration.setLogoutUrl("logoutUrl");
    configuration.setProvider("provider");
    configuration.setAuthUrl("authUrl");
    configuration.setAuthUrlScope("authUrlScope");
    configuration.setClientSecret("secret");
    configuration.setGroupsEndpoint("groupsEndpoint");
    configuration.setIdpApiUri("idpApiUri");
    configuration.setRedirectUrl("redirectUrl");
    configuration.setTenantId("tenantId");
    configuration.setTokenUrl("tokenUrl");
    configuration.setUsersEndpoint("userEndpoint");

    // THEN
    assertThatCode(() -> validator.validate(configuration)).doesNotThrowAnyException();
  }

  @Test
  public void shouldNotValidateConfiguration_givenMissingConfigParam() {
    // GIVEN
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setAppIdUri("appIdUri");
    configuration.setClientId("clientId");
    configuration.setConfigurationUrl("configUrl");
    configuration.setLogoutUrl("logoutUrl");
    configuration.setProvider("provider");
    configuration.setAuthUrl("authUrl");
    configuration.setAuthUrlScope("authUrlScope");
    configuration.setClientSecret("secret");
    configuration.setGroupsEndpoint("groupsEndpoint");
    configuration.setIdpApiUri("idpApiUri");
    configuration.setRedirectUrl("redirectUrl");
    configuration.setTenantId("tenantId");
    configuration.setTokenUrl("tokenUrl");

    // THEN
    assertThatExceptionOfType(TenantConfigurationException.class)
        .isThrownBy(() -> validator.validate(configuration))
        .withMessageContaining("UsersEndpoint");
  }
}
