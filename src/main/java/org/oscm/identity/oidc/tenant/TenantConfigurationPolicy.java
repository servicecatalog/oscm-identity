package org.oscm.identity.oidc.tenant;

public interface TenantConfigurationPolicy {

    TenantConfiguration loadTenant(String tenantId);
}
