/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 16, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.oidc.response.validation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthTokenValidatorTest {

  @Autowired private AuthTokenValidator validator;

  @Test
  @Disabled("Needs reimplementation")
  // TODO: Setup test with JWT#create
  public void shouldConfirmTokenValidity_givenValidToken() {
    String validToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJz"
            + "dWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF"
            + "0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6"
            + "yJV_adQssw5c";

    TokenValidationResult result = validator.validate(validToken);

    assertThat(result).extracting(TokenValidationResult::isValid).isEqualTo(true);
    assertThat(result.getValidationFailureReason()).isBlank();

    fail("Need reimplementation");
  }

  @Test
  @Disabled("Needs reimplementation")
  public void shouldNotConfirmTokenValidity_givenValidToken() {
    String invalidToken = "somedatathatarenotvalidforthistest";

    TokenValidationResult result = validator.validate(invalidToken);

    assertThat(result).extracting(TokenValidationResult::isValid).isEqualTo(false);
    assertThat(result.getValidationFailureReason()).isNotEmpty();
  }
}
