/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 18, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Component
public class IdTokenAudienceValidationStrategy extends TokenValidationStrategy {

  private TenantService tenantService;

  @Autowired
  public IdTokenAudienceValidationStrategy(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  // FIXME: Determine, from where audience for acccessToken validation should be taken
  // FIXME: Let's try not to add new tenant property for it as it is not so straightforward
  // FIXME: to configure
  @Override
  public void execute(TokenValidationRequest request) throws ValidationException {
    TenantConfiguration tenantConfiguration =
        tenantService.loadTenant(Optional.ofNullable(request.getTenantId()));

    if(request.getDecodedIdToken() == null) {
      logIDTokenNotFound(this);
      return;
    }

    if (!request.getDecodedIdToken().getAudience().contains(tenantConfiguration.getClientId()))
      throw new ValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Id token's audience does not contain client ID from current tenant configuration";
  }
}
