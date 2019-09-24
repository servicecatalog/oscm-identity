/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;

/** Abstraction class for OIDC tokens validation strategies */
@Slf4j
public abstract class TokenValidationStrategy {

  /**
   * Runs validation for provided decoded OIDC token
   *
   * @param decodedToken decoded JWT token
   * @param tenantConfiguration configuration for selected tenant
   * @throws TokenValidationException
   */
  public abstract void execute(DecodedJWT decodedToken, TenantConfiguration tenantConfiguration)
      throws TokenValidationException;

  /**
   * Returns validation failure message
   *
   * @return failure message
   */
  public abstract String getFailureMessage();
}
