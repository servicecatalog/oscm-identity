/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 29, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.error.IdTokenValidationException;
import org.oscm.identity.model.request.TokenDetails;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdTokenNonceValidationStrategyTest {

  private IdTokenNonceValidationStrategy strategy;

  @BeforeEach
  public void setUp() {
    strategy = new IdTokenNonceValidationStrategy();
  }
  @Test
  @SneakyThrows
  public void shouldValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setNonce("testNonce");

    String token = JWT.create().withClaim("nonce", "testNonce").sign(Algorithm.none());

    assertThatCode(() -> strategy.execute(JWT.decode(token), configuration)).doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotValidateRequest() {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setNonce("someOtherNonce");

    String token = JWT.create().withClaim("nonce", "testNonce").sign(Algorithm.none());

    assertThatExceptionOfType(IdTokenValidationException.class)
        .isThrownBy(() -> strategy.execute(JWT.decode(token), configuration));
  }
}
