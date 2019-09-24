/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 19, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.stereotype.Component;

@Component
public class IdTokenNonceValidationStrategy extends TokenValidationStrategy {

  private final String NONCE_CLAIM_NAME = "nonce";

  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws IdTokenValidationException {
    if (!tenantConfiguration.getNonce().equals(decodedToken.getClaim(NONCE_CLAIM_NAME).asString()))
      throw new IdTokenValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Nonce provided in a request does not match the one from ID Token";
  }
}
