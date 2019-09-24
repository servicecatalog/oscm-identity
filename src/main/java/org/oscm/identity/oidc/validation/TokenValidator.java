/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.request.TokenDetails;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.oidc.validation.strategy.TokenValidationStrategy;
import org.oscm.identity.service.TenantService;

import java.util.List;
import java.util.Optional;

/** Validator class for OpenID Authentication Token */
@Slf4j
public abstract class TokenValidator {

  private DecodedJWT decodedToken;
  private TenantConfiguration tenantConfiguration;

  TokenValidator(String tenantId, TokenDetails tokenDetails, TenantService tenantService)
      throws TokenValidationException {
    this.decodedToken = decodeToken(tokenDetails);
    this.tenantConfiguration = tenantService.loadTenant(Optional.of(tenantId));
  }

  /**
   * Declaration of validator-specific validator strategy getter
   *
   * @return list of validation strategies, specific for the implementation of Validator
   */
  abstract List<TokenValidationStrategy> getValidationStrategies();

  /**
   * Transforms string token into DecodedJWT
   *
   * @param tokenDetails token type along with encoded token
   * @return decoded JWT token
   * @throws TokenValidationException
   */
  private DecodedJWT decodeToken(TokenDetails tokenDetails) throws TokenValidationException {
    try {
      return JWT.decode(tokenDetails.getToken());
    } catch (JWTDecodeException e) {
      log.error(e.getMessage(), e);
      throw new TokenValidationException(e.getMessage(), e);
    }
  }

  /**
   * Executes provided strategies against given token and tenant configuration
   *
   * @throws TokenValidationException
   */
  public final void validate() throws TokenValidationException {
    for (TokenValidationStrategy strategy : getValidationStrategies()) {
      strategy.execute(decodedToken, tenantConfiguration);
    }
  }
}
