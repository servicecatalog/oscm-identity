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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.validation.AuthTokenValidator;
import org.oscm.identity.oidc.validation.strategy.IdTokenNonceValidationStrategy;
import org.oscm.identity.oidc.validation.strategy.TokenValidationStrategy;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class IdTokenNonceValidationStrategyTest {

  private TokenValidationStrategy strategy;
  private TokenValidationRequest request;

  @BeforeEach
  public void setUp() {
    strategy = new IdTokenNonceValidationStrategy();
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

  @Test
  public void shouldNotValidateRequest_givenNoToken() {
    try {
      strategy.execute(TokenValidationRequest.of().build());
    } catch (ValidationException e) {
      fail(e.getMessage());
    }
  }
}
