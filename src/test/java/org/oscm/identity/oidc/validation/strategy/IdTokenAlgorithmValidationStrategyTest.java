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
import org.oscm.identity.oidc.validation.strategy.IdTokenAlgorithmValidationStrategy;
import org.oscm.identity.oidc.validation.strategy.TokenValidationStrategy;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class IdTokenAlgorithmValidationStrategyTest {

  private IdTokenAlgorithmValidationStrategy strategy;
  private TokenValidationRequest request;

  @BeforeEach
  public void setUp() {
    strategy = new IdTokenAlgorithmValidationStrategy();
    strategy.setExpectedAlgorithmType("RS256");
  }

  @Test
  @SneakyThrows
  public void shouldValidateRequest() {
    String rsaToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIx"
            + "MjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiw"
            + "iYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn"
            + "0.POstGetfAytaZS82wHcjoTyoqhMyxXiWdR7Nn"
            + "7A29DNSl0EiXLdwJ6xC6AfgZWF1bOsS_TuYI3OG"
            + "85AmiExREkrS6tDfTQ2B3WXlrr-wp5AokiRbz3_"
            + "oB4OxG-W9KcEEbDRcZc0nH3L7LzYptiy1PtAylQ"
            + "GxHTWZXtGz4ht0bAecBgmpdgXMguEIcoqPJ1n3p"
            + "IWk_dUZegpqx0Lka21H6XxUTxiy8OcaarA8zdnP"
            + "UnV6AmNP3ecFawIFYdvJB_cm-GvpCSbr8G8y_Ml"
            + "lj8f4x9nBH8pQux89_6gUY618iYv7tuPWBFfEbL"
            + "xtF2pZS6YC1aSfLQxeNe8djT9YjpvRZA";
    request =
        TokenValidationRequest.of()
            .idToken(rsaToken)
            .accessToken(rsaToken)
            .build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatCode(() -> strategy.execute(request)).doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotValidateRequest() {
    String token = JWT.create().sign(Algorithm.none());
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
