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
import org.oscm.identity.model.json.TokenDetailsDTO;
import org.oscm.identity.oidc.validation.strategy.AccessTokenAlgorithmValidationStrategy;
import org.oscm.identity.oidc.validation.strategy.AccessTokenExpirationTimeValidationStrategy;
import org.oscm.identity.oidc.validation.strategy.TokenValidationStrategy;
import org.oscm.identity.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AccessTokenValidator extends TokenValidator {

  private AccessTokenAlgorithmValidationStrategy accessTokenAlgorithmValidationStrategy;
  private AccessTokenExpirationTimeValidationStrategy accessTokenExpirationTimeValidationStrategy;

  AccessTokenValidator(String tenantId, TokenDetailsDTO tokenDetails, TenantService tenantService)
      throws TokenValidationException {
    super(tenantId, tokenDetails, tenantService);
  }

  @Override
  List<TokenValidationStrategy> getValidationStrategies() {
    return Lists.newArrayList(
        accessTokenAlgorithmValidationStrategy, accessTokenExpirationTimeValidationStrategy);
  }

  @Autowired
  public final void setAccessTokenAlgorithmValidationStrategy(
      AccessTokenAlgorithmValidationStrategy accessTokenAlgorithmValidationStrategy) {
    this.accessTokenAlgorithmValidationStrategy = accessTokenAlgorithmValidationStrategy;
  }

  @Autowired
  public final void setAccessTokenExpirationTimeValidationStrategy(
      AccessTokenExpirationTimeValidationStrategy accessTokenExpirationTimeValidationStrategy) {
    this.accessTokenExpirationTimeValidationStrategy = accessTokenExpirationTimeValidationStrategy;
  }
}
