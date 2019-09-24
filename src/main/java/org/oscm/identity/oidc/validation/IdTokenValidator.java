/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 20-09-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import org.assertj.core.util.Lists;
import org.oscm.identity.error.TokenValidationException;
import org.oscm.identity.model.request.TokenDetails;
import org.oscm.identity.oidc.validation.strategy.*;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IdTokenValidator extends TokenValidator {

  private IdTokenAlgorithmValidationStrategy idTokenAlgorithmValidationStrategy;
  private IdTokenAudienceValidationStrategy idTokenAudienceValidationStrategy;
  private IdTokenExpirationTimeValidationStrategy idTokenExpirationTimeValidationStrategy;
  private IdTokenISSValidationStrategy idTokenISSValidationStrategy;
  private IdTokenNonceValidationStrategy idTokenNonceValidationStrategy;

  public IdTokenValidator(String tenantId, TokenDetails tokenDetails, TenantService tenantService)
      throws TokenValidationException {
    super(tenantId, tokenDetails, tenantService);
  }

  @Override
  List<TokenValidationStrategy> getValidationStrategies() {
    return Lists.newArrayList(
        idTokenAlgorithmValidationStrategy,
        idTokenAudienceValidationStrategy,
        idTokenExpirationTimeValidationStrategy,
        idTokenISSValidationStrategy,
        idTokenNonceValidationStrategy);
  }

  @Autowired
  public final void setIdTokenAlgorithmValidationStrategy(
      IdTokenAlgorithmValidationStrategy idTokenAlgorithmValidationStrategy) {
    this.idTokenAlgorithmValidationStrategy = idTokenAlgorithmValidationStrategy;
  }

  @Autowired
  public final void setIdTokenAudienceValidationStrategy(
      IdTokenAudienceValidationStrategy idTokenAudienceValidationStrategy) {
    this.idTokenAudienceValidationStrategy = idTokenAudienceValidationStrategy;
  }

  @Autowired
  public final void setIdTokenExpirationTimeValidationStrategy(
      IdTokenExpirationTimeValidationStrategy idTokenExpirationTimeValidationStrategy) {
    this.idTokenExpirationTimeValidationStrategy = idTokenExpirationTimeValidationStrategy;
  }

  @Autowired
  public final void setIdTokenISSValidationStrategy(
      IdTokenISSValidationStrategy idTokenISSValidationStrategy) {
    this.idTokenISSValidationStrategy = idTokenISSValidationStrategy;
  }

  @Autowired
  public final void setIdTokenNonceValidationStrategy(
      IdTokenNonceValidationStrategy idTokenNonceValidationStrategy) {
    this.idTokenNonceValidationStrategy = idTokenNonceValidationStrategy;
  }
}