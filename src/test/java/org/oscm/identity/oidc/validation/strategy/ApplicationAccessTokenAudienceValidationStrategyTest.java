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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oscm.identity.error.AccessTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ApplicationAccessTokenAudienceValidationStrategyTest {

  private ApplicationAccessTokenAudienceValidationStrategy strategy;

  @BeforeEach
  public void setUp() {
    strategy = new ApplicationAccessTokenAudienceValidationStrategy();
  }

  @Test
  public void shouldValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setAppIdUri("appIdUri");
    String token = JWT.create().withAudience("appIdUri").sign(Algorithm.none());

    assertThatCode(() -> strategy.execute(JWT.decode(token), configuration))
        .doesNotThrowAnyException();
  }

  @Test
  public void shouldNotValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setClientId("nonValidValue");
    String token = JWT.create().withAudience("appIdUri").sign(Algorithm.none());

    assertThatExceptionOfType(AccessTokenValidationException.class)
        .isThrownBy(() -> strategy.execute(JWT.decode(token), configuration));
  }
}
