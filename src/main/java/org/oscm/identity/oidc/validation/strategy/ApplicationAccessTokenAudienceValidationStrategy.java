/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 08-10-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.oscm.identity.error.AccessTokenValidationException;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ApplicationAccessTokenAudienceValidationStrategy extends TokenValidationStrategy {

  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws TokenValidationException {
    if (!decodedToken.getAudience().contains(tenantConfiguration.getAppIdUri()))
      throw new AccessTokenValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Access token's audience doesn't match the 'appIdUri' in current tenant's configuration";
  }
}
