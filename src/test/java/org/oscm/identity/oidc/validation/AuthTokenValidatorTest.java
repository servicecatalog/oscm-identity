/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.validation;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oscm.identity.model.request.TokenValidationRequest;
import org.oscm.identity.oidc.validation.strategy.*;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class AuthTokenValidatorTest {

  @Mock private AccessTokenAlgorithmValidationStrategy accessTokenAlgorithmValidationStrategy;
  @Mock private AccessTokenExpirationTimeValidationStrategy accessTokenExpirationTimeValidationStrategy;
  @Mock private IdTokenAlgorithmValidationStrategy idTokenAlgorithmValidationStrategy;
  @Mock private IdTokenAudienceValidationStrategy idTokenAudienceValidationStrategy;
  @Mock private IdTokenExpirationTimeValidationStrategy idTokenExpirationTimeValidationStrategy;
  @Mock private IdTokenISSValidationStrategy idTokenIssValidationStrategy;
  @Mock private IdTokenNonceValidationStrategy idTokenNonceValidationStrategy;
  @InjectMocks private AuthTokenValidator validator;

  @Test
  @SneakyThrows
  public void shouldConfirmTokenValidity_givenValidToken() {
    String validToken =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gR"
            + "G9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiw"
            + "iZXhwIjo1MDE2MjM5MDIyLCJhdWQiOiJ0ZXN0Q2xpZW50S"
            + "WQiLCJpc3MiOiJ0ZXN0SXNzdXJlciJ9.RRFOQJ7YjpYoV6"
            + "mZ7iq7Bm__3gvs6tWyPJghGRK6YeLQEEikM8K7_5mOPl-u"
            + "5XrW8zlR4uXxnQ8V-QO8IBjLAwDgEE-TEOqJHeYe3iO_b0"
            + "DCJ2W5zpvEjcCjGP_itBXGkk2hxKB3951jM_xSYZrWCwMd"
            + "H8cZZ6w6LNU4N2oLd6JwoOheUaabIhfvpsQb5vF3iM7k98"
            + "U6lyi7Yu6pWdDFzqMc6ijsRRNu4SjCm_Vqhnw4G0qIRDij"
            + "RYeI7JRQ8PivPRDHpoysvNjjW3DUOsCdQHzrl-utA0S4e4"
            + "sIVyxQW93KXJLAXlP8UVAxaShPrrKcEBk4mutZaIoctTNosrokrw";

    TokenValidationRequest request =
        TokenValidationRequest.of()
            .idToken(validToken)
            .tenantId("default")
            .build();

    validator.validate(request);

    assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
  }

  @Test
  @SneakyThrows
  public void shouldNotConfirmTokenValidity_givenValidToken() {
    String invalidToken = "somedatathatarenotvalidforthistest";
    TokenValidationRequest request =
        TokenValidationRequest.of()
            .idToken(invalidToken)
            .tenantId("default")
            .build();

    assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> validator.validate(request));
  }
}
