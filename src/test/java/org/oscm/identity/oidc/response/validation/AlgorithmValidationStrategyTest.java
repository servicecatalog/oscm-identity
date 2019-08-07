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
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.model.request.validation.AlgorithmValidationStrategy;
import org.oscm.identity.model.request.validation.TokenValidationStrategy;

import javax.xml.bind.ValidationException;
import java.sql.Date;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

public class AlgorithmValidationStrategyTest {

  private TokenValidationStrategy strategy;
  private TokenValidationRequest request;

  @BeforeEach
  public void setUp() {
    strategy = new AlgorithmValidationStrategy("RS256");
  }

  @Test
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
  public void shouldNotValidateRequest() {
    String token = JWT.create().sign(Algorithm.none());
    request = TokenValidationRequest.of().idToken(token).build();
    request = AuthTokenValidator.decodeTokens(request);

    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(() -> strategy.execute(request));
  }
}
