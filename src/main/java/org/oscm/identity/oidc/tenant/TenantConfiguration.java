package org.oscm.identity.oidc.tenant;

import lombok.Data;

@Data
public class TenantConfiguration {

    private String provider;
    private String tenantId;
    private String clientId;
    private String authUrl;
    private String idTokenRedirectUrl;
}
