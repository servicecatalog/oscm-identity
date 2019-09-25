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
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdTokenAlgorithmValidationStrategy extends TokenValidationStrategy {

  @Override
  public void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws IdTokenValidationException {
    if (!decodedToken.getAlgorithm().equalsIgnoreCase(expectedAlgorithmType))
      throw new IdTokenValidationException(getFailureMessage());
  }

  @Override
  public String getFailureMessage() {
    return "Id token signing algorithm type does not match the expected one";
  }

  @Value("${auth.signing.algorithm.type}")
  @Setter
  private String expectedAlgorithmType;
}
