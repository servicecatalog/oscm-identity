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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

    @Mock
    private TenantConfigurationPolicy policy;

    @InjectMocks
    private TenantService service;

    @Test
    public void testLoadTenant_tenantNotPresent_defaultTenantIsLoaded() {

        //given
        TenantConfiguration defaultConfiguration = givenDefaultTenantConfiguration();
        Mockito.when(policy.loadTenant("default")).thenReturn(defaultConfiguration);

        //when
        TenantConfiguration tenantConfiguration = service.loadTenant(Optional.empty());

        //then
        Assertions.assertThat(tenantConfiguration).isEqualTo(defaultConfiguration);
    }

    @Test
    public void testLoadTenant_tenantPresent_configuredTenantIsLoaded() {

        //given
        String tenantId = "qwe4rt6y";
        TenantConfiguration simpleConfiguration = givenSimpleTenantConfiguration(tenantId);
        Mockito.when(policy.loadTenant(Mockito.anyString())).thenReturn(simpleConfiguration);

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
        return configuration;
    }

    private TenantConfiguration givenSimpleTenantConfiguration(String tenantId) {
        TenantConfiguration configuration = new TenantConfiguration();
        configuration.setProvider("other-provider");
        configuration.setClientId("sample-client-id");
        configuration.setTenantId(tenantId);
        return configuration;
    }
}
