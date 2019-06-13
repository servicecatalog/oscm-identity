package org.oscm.identity.service;

import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.tenant.TenantConfigurationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantConfigurationPolicy policy;

    public TenantConfiguration loadTenant(Optional<String> tenant){

        if(tenant.isPresent()){
            return policy.loadTenant(tenant.get());
        } else{
            return policy.loadTenant("default");
        }
    }
}
