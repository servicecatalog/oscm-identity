/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 17, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Setter;
import org.oscm.identity.error.AccessTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenAlgorithmValidationStrategy extends TokenValidationStrategy {

  @Value("${auth.signing.algorithm.type}")
  @Setter
  private String expectedAlgorithmType;

  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws AccessTokenValidationException {
    if (!decodedToken.getAlgorithm().equalsIgnoreCase(expectedAlgorithmType))
      throw new AccessTokenValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Access token signing algorithm type does not match the expected one";
  }
}
