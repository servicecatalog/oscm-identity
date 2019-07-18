/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 18, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.oidc.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Slf4j
@Component
public class AudienceValidationStrategy implements TokenValidationStrategy {

  private static final String VALIDATION_FAILURE_MESSAGE =
      "Token's audience " + "does not contain client ID from current tenant configuraion";

  private TenantService tenantService;

  @Autowired
  public AudienceValidationStrategy(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    TenantConfiguration tenantConfiguration =
        tenantService.loadTenant(Optional.ofNullable(request.getTenantId()));

    if (!request.getDecodedToken().getAudience().contains(tenantConfiguration.getClientId()))
      throw new ValidationException(VALIDATION_FAILURE_MESSAGE);
  }
}
