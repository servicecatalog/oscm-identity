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
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.validation.AuthTokenValidator;
import org.oscm.identity.oidc.validation.strategy.IdTokenExpirationTimeValidationStrategy;
import org.oscm.identity.oidc.validation.strategy.TokenValidationStrategy;

import javax.xml.bind.ValidationException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class IdTokenExpirationTimeValidationStrategyTest {

  private TokenValidationStrategy strategy;
  private TokenValidationRequest request;

  @BeforeEach
  public void setUp() {
    strategy = new IdTokenExpirationTimeValidationStrategy();
  }

  @Test
  @SneakyThrows
  public void shouldValidateRequest() {
    String token =
        JWT.create()
            .withExpiresAt(Date.from(LocalDateTime.now().plusYears(2).toInstant(ZoneOffset.UTC)))
            .sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).accessToken(token).build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotValidateRequest() {
    String token =
        JWT.create()
            .withExpiresAt(Date.from(LocalDateTime.now().minusYears(2).toInstant(ZoneOffset.UTC)))
            .sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }

  @Test
  public void shouldNotValidateRequest_givenNoToken() {
    try {
      strategy.execute(TokenValidationRequest.of().build());
    } catch (ValidationException e) {
      fail(e.getMessage());
    }
  }
}
