/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: 20-09-2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation.strategy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oscm.identity.error.AccessTokenValidationException;
import org.oscm.identity.oidc.tenant.TenantConfiguration;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class AccessTokenExpirationTimeValidationStrategyTest {

  private TokenValidationStrategy strategy;
  private TenantConfiguration tenantConfiguration;

  @BeforeEach
  public void setUp() {
    strategy = new AccessTokenExpirationTimeValidationStrategy();
    tenantConfiguration = new TenantConfiguration();
  }

  @Test
  @SneakyThrows
  public void shouldValidateRequest() {
    String token =
        JWT.create()
            .withExpiresAt(Date.from(LocalDateTime.now().plusYears(2).toInstant(ZoneOffset.UTC)))
            .sign(Algorithm.none());

    assertThatCode(() -> strategy.execute(JWT.decode(token), tenantConfiguration))
        .doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotValidateRequest() {
    String token =
        JWT.create()
            .withExpiresAt(Date.from(LocalDateTime.now().minusYears(2).toInstant(ZoneOffset.UTC)))
            .sign(Algorithm.none());

    assertThatExceptionOfType(AccessTokenValidationException.class)
        .isThrownBy(() -> strategy.execute(JWT.decode(token), tenantConfiguration));
  }
}
