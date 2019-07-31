/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 29, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oscm.identity.oidc.request.TokenValidationRequest;

import javax.xml.bind.ValidationException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class ExpirationTimeValidationStrategyTest {

  private TokenValidationStrategy strategy;
  private TokenValidationRequest request;

  @BeforeEach
  public void setUp() {
    strategy = new ExpirationTimeValidationStrategy();
  }

  @Test
  public void shouldValidateRequest() {
    String token =
        JWT.create()
            .withExpiresAt(Date.from(LocalDateTime.now().plusYears(2).toInstant(ZoneOffset.UTC)))
            .sign(Algorithm.none());
    request = TokenValidationRequest.of().token(token).build();
    request.setDecodedToken(JWT.decode(request.getToken()));

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  public void shouldNotValidateRequest() {
    String token =
        JWT.create()
            .withExpiresAt(Date.from(LocalDateTime.now().minusYears(2).toInstant(ZoneOffset.UTC)))
            .sign(Algorithm.none());
    request = TokenValidationRequest.of().token(token).build();
    request.setDecodedToken(JWT.decode(request.getToken()));

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }
}
