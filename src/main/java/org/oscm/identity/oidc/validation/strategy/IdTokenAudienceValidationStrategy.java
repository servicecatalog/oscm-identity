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

import com.auth0.jwt.interfaces.DecodedJWT;
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.stereotype.Component;

@Component
public class IdTokenAudienceValidationStrategy extends TokenValidationStrategy {

  // FIXME: Determine, from where audience for acccessToken validation should be taken
  // FIXME: Let's try not to add new tenant property for it as it is not so straightforward
  // FIXME: to configure
  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws IdTokenValidationException {
    if (!decodedToken.getAudience().contains(tenantConfiguration.getClientId()))
      throw new IdTokenValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Id token's audience does not contain client ID from current tenant configuration";
  }
}
