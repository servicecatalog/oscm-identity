package org.oscm.identity.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.tenant.TenantConfigurationPolicy;
import org.oscm.identity.oidc.validation.TenantConfigurationValidator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

    @Mock
    private TenantConfigurationPolicy policy;
    @Mock
    private TenantConfigurationValidator validator;
    @InjectMocks
    private TenantService service;

    @Test
    public void testLoadTenant_tenantNotPresent_defaultTenantIsLoaded() {

        //given
        TenantConfiguration defaultConfiguration = givenDefaultTenantConfiguration();
        when(policy.loadTenant("default")).thenReturn(defaultConfiguration);
        doCallRealMethod().when(validator).validate(any());

        //when
        TenantConfiguration tenantConfiguration = service.loadTenant(Optional.empty());

        //then
        Assertions.assertThat(tenantConfiguration).isEqualTo(defaultConfiguration);
    }

    @Test
    public void testLoadTenant_tenantPresent_configuredTenantIsLoaded() {

        //given
        String tenantId = "qwe4rt6y";
        TenantConfiguration simpleConfiguration = givenFullTenantConfiguration(tenantId);
        when(policy.loadTenant(Mockito.anyString())).thenReturn(simpleConfiguration);
        doCallRealMethod().when(validator).validate(any());

        //when
        TenantConfiguration tenantConfiguration = service.loadTenant(Optional.of(tenantId));

        //then
        Assertions.assertThat(tenantConfiguration).isEqualTo(simpleConfiguration);
    }

    private TenantConfiguration givenDefaultTenantConfiguration() {
        TenantConfiguration configuration = new TenantConfiguration();
        configuration.setProvider("default");
        configuration.setClientId("sample-client-id");
        configuration.setTenantId("default");
        configuration.setConfigurationUrl("configUrl");
        configuration.setLogoutUrl("logoutUrl");
        configuration.setAuthUrl("authUrl");
        configuration.setAuthUrlScope("authUrlScope");
        configuration.setClientSecret("secret");
        configuration.setGroupsEndpoint("groupsEndpoint");
        configuration.setIdpApiUri("idpApiUri");
        configuration.setRedirectUrl("redirectUrl");
        configuration.setTokenUrl("tokenUrl");
        configuration.setUsersEndpoint("userEndpoint");
        return configuration;
    }

    private TenantConfiguration givenFullTenantConfiguration(String tenantId) {
        TenantConfiguration configuration = new TenantConfiguration();
        configuration.setProvider("other-provider");
        configuration.setClientId("sample-client-id");
        configuration.setTenantId(tenantId);
        configuration.setConfigurationUrl("configUrl");
        configuration.setLogoutUrl("logoutUrl");
        configuration.setAuthUrl("authUrl");
        configuration.setAuthUrlScope("authUrlScope");
        configuration.setClientSecret("secret");
        configuration.setGroupsEndpoint("groupsEndpoint");
        configuration.setIdpApiUri("idpApiUri");
        configuration.setRedirectUrl("redirectUrl");
        configuration.setTokenUrl("tokenUrl");
        configuration.setUsersEndpoint("userEndpoint");
        return configuration;
    }
}
