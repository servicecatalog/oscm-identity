/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 29, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.validation.NonceValidationStrategy;
import org.oscm.identity.oidc.validation.TokenValidationStrategy;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class NonceValidationStrategyTest {

  private TokenValidationStrategy strategy;
  private TokenValidationRequest request;

  @BeforeEach
  public void setUp() {
    strategy = new NonceValidationStrategy();
  }

  @Test
  public void shouldValidateRequest() {
    String token = JWT.create().withClaim("nonce", "testNonce").sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).nonce("testNonce").build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  public void shouldNotValidateRequest() {
    String token = JWT.create().withClaim("nonce", "testNonce").sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).nonce("nonMatchingNonce").build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }
}
