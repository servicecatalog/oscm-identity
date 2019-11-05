package org.oscm.identity.service;

import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.tenant.TenantConfigurationPolicy;
import org.oscm.identity.oidc.validation.TenantConfigurationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantService {

  @Autowired private TenantConfigurationPolicy policy;
  @Autowired private TenantConfigurationValidator validator;

  public TenantConfiguration loadTenant(Optional<String> tenant) {

    TenantConfiguration configuration;

    if (tenant.isPresent()) {
      configuration = policy.loadTenant(tenant.get());
    } else {
      configuration = policy.loadTenant("default");
    }

    validator.validate(configuration);
    return configuration;
  }
}
