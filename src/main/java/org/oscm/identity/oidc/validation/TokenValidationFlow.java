/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 19-09-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.request.TokenDetails;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Builder-style class that is used to initiate the flow of validating JWT tokens */
@Component
public class TokenValidationFlow {

  private static final String TOKEN_TYPE_NOT_SUPPORTED_ERROR_MESSAGE =
      "Token type of %s is not supported";

  private BeanFactory beanFactory;
  private TenantService tenantService;
  private String tenantIdentifier;

  @Autowired
  public TokenValidationFlow(BeanFactory beanFactory, TenantService tenantService) {
    this.beanFactory = beanFactory;
    this.tenantService = tenantService;
  }

  /**
   * Sets up tenant identifier for the current validation flow
   *
   * @param tenantIdentifier tenant identifier to be set
   * @return
   */
  public TokenValidationFlow forTenantOf(String tenantIdentifier) {
    this.tenantIdentifier = tenantIdentifier;
    return this;
  }
  /**
   * Returns proper TokenValidator implementation, basing on TokenDetails contents
   *
   * @param tokenDetails wrapper class containing encoded JWT token, its type and all the info
   *     necessary for its validation
   * @return token validator implementation for token that is requested to be validated
   * @throws TokenValidationException
   */
  public TokenValidator withTokenFrom(TokenDetails tokenDetails) throws TokenValidationException {
    switch (tokenDetails.getTokenType()) {
      case ID:
        return beanFactory.getBean(
            IdTokenValidator.class, tenantIdentifier, tokenDetails, tenantService);
      case ACCESS:
        return beanFactory.getBean(
            AccessTokenValidator.class, tenantIdentifier, tokenDetails, tenantService);
      default:
        throw new TokenValidationException(
            String.format(TOKEN_TYPE_NOT_SUPPORTED_ERROR_MESSAGE, tokenDetails.getTokenType()));
    }
  }
}
